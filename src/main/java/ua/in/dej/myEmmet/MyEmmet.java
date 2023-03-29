package ua.in.dej.myEmmet;

// import com.intellij.ide.impl.DataManagerImpl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.project.Project;
import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import org.jetbrains.annotations.NotNull;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ua.in.dej.myEmmet.utils.BasicLeveledLogger;
import ua.in.dej.myEmmet.utils.BasicLeveledLoggerFactory;
// import sun.org.mozilla.javascript.internal.NativeObject; // 1.7

import javax.script.*;
import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fima on 28.04.14.
 */
@SuppressWarnings("FieldCanBeLocal")
public class MyEmmet extends AnAction {
	
	// ===== ===== ===== ===== [Constants] ===== ===== ===== ===== //
	
	/** Logger */
	// public static final Logger log = LoggerFactory.getLogger(MyEmmet.class);
	
	/** The default log title for this plugin */
	public static final String LOG_TITLE = "[EmmetEverywhere] ";
	
	/** The MIME type to load JavaScript engine */
	public static final String ENGINE_MIME_TYPE = "application/javascript";
	
	/** The name to load JavaScript engine */
	public static final String ENGINE_NAME = "js";
	
	/** The resource path of [emmet.js] */
	public static final String EMMET_SCRIPT_PATH = "/emmet.js";
	
	/** The text pattern of output of emmet expanding operation */
	public static final Pattern EMMET_OUTPUT_PATTERN = Pattern.compile(
			"^((.|\\s)*)\",\"selectStart\":(.*),\"selectStop\":(.*)$", Pattern.MULTILINE
	);
	
	// ===== ===== ===== ===== [Static Variables] ===== ===== ===== ===== //
	
	/** The script engine */
	private static Invocable myInv;
	
	/** Whether the emmet script has been loaded */
	private static boolean emmetScriptLoaded;
	
	/** The basic log level of this plugin */
	private static int basicLogLevel = BasicLeveledLogger.LogLevels.INFO;
	
	/** Logger */
	private static BasicLeveledLogger log;
	
	// ===== ===== ===== ===== [Instant Variables] ===== ===== ===== ===== //
	
	/** Whether to catch all the exception when init plugin instance */
	private final boolean safelyInit;
	
	/** Whether to catch all the exception when perform action logic */
	private final boolean safelyPerform;
	
	// ===== ===== ===== ===== [Constructor] ===== ===== ===== ===== //
	
	public MyEmmet() {
		this(false, true);
		
	}
	
	public MyEmmet(boolean safelyInit, boolean safelyPerform) {
		// NOTE The duplicated initialization operation won't create huge cost, so this code is not synchronized
		this.safelyInit = safelyInit;
		this.safelyPerform = safelyPerform;
		if (this.safelyInit) {
			try {
				initLogger();
				initScriptEngine();
				loadEmmetScript((ScriptEngine) myInv);
				
			} catch (Exception e) {
				log.warn("Fail to init plugin, unknown exception happened: {}", getBriefErrorInfo(e), e);
				
			}
			
		} else {
			initLogger();
			initScriptEngine();
			loadEmmetScript((ScriptEngine) myInv);
			
		}
		
	}
	
	// ===== ===== ===== ===== [Override Method] ===== ===== ===== ===== //
	
	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		if (this.safelyPerform) {
			try {
				tryGetExpressionAndDoExpand(event);
				
			} catch (Exception e) {
				log.warn("Fail to expand emmet expression, unknown exception happened: {}", getBriefErrorInfo(e), e);
				
			}
			
		} else {
			tryGetExpressionAndDoExpand(event);
			
		}
		
	}
	
	// ===== ===== ===== ===== [Instant Operation Method] ===== ===== ===== ===== //
	
	/** Try to get the emmet expression of selection or at left side of caret, do expand to it and output result */
	private void tryGetExpressionAndDoExpand(@NotNull AnActionEvent event) {
		// STEP Print log
		log.log("{}Action event received", LOG_TITLE);
		
		// STEP Get project, editor and document
		final Project project = event.getProject();
		FileEditor fileEditor = event.getData(PlatformDataKeys.FILE_EDITOR);
		if (fileEditor == null) {
			return;
		}
		Editor editor = ((PsiAwareTextEditorImpl) fileEditor).getEditor();
		Document document = editor.getDocument();
		
		// STEP Check if document is writable
		if (! document.isWritable()) {
			log.log("{}Document is not writable, action break", LOG_TITLE);
			return;
			
		}
		
		// STEP Get the full text of document, caret position and line start
		// NOTE The emmet only supports a single line expression, so we need to get the line start position
		String fullText = document.getText();
		CaretModel caretModel = editor.getCaretModel();
		int caretPosition = caretModel.getOffset();
		int lineStart = caretModel.getVisualLineStart();
		
		// STEP Declare core variables
		// 1.7
		// NativeObject outputData = null;
		// 1.8
		int textStart = caretPosition;
		int textEnd = caretPosition;
		int newSelectionStart = caretPosition;
		int newSelectionStop = caretPosition;
		StringBuilder inputStringBuilder = new StringBuilder();
		
		// NOTE Get selection information
		// CHANGED 2023/3/29 11:31 BY.ZhouYi Add support for selection expanding
		final SelectionModel selectionModel = editor.getSelectionModel();
		int selectionStart = selectionModel.getSelectionStart();
		int selectionEnd = selectionModel.getSelectionEnd();
		String selectedText = selectionModel.getSelectedText();
		boolean isSelectionNotBlank = (selectedText != null) && (! selectedText.trim().isEmpty());
		log.log("{}Get selected text at [{} - {}]: {}", LOG_TITLE, selectionStart, selectionEnd, isSelectionNotBlank ? selectedText : "");
		
		// NOTE Check if there is a non-blank text in the selection
		// BRANCH If there is, use it as the value for emmet
		if (isSelectionNotBlank) {
			// NOTE Use the max function to ensure that the caret position is at the end of the selection and prevent a reversed selection
			inputStringBuilder.append(selectedText);
			caretPosition = Math.max(selectionStart, selectionEnd);
			textStart = caretPosition - selectedText.length();
			textEnd = caretPosition;
			
		// BRANCH If there is not, fetch the expression text from the left of caret
		} else {
			// SUBSTEP Check if caret is at the end of a emmet expression
			if (! isCaretPositionValid(fullText, caretPosition)) {
				log.log("{}Fail to expand emmet, caret is not at the end of a emmet expression", LOG_TITLE);
				return;
				
			}
			
			// SUBSTEP Check and append chars from the left of caret one by one
			boolean inBrace = false;
			boolean inSquare = false;
			char ch = '-'; // Just a meaningless initial value to pass the {@link Character#isWhitespace(char)} check
			while ((textStart >= lineStart) && (inBrace || inSquare || (! Character.isWhitespace(ch)))) {
				textStart --;
				ch = fullText.charAt(textStart);
				if ((! inSquare) && (! inBrace) && ch == '}') {
					inBrace = true;
					
				} else if (inBrace && ch == '{') {
					inBrace = false;
					
				} else if ((! inSquare) && (! inBrace) && ch == ']') {
					inSquare = true;
					
				} else if (inSquare && ch == '[') {
					inSquare = false;
					
				}
				
			}
			textStart ++;
			inputStringBuilder.append(fullText, textStart, textEnd);
			
		}
		
		// STEP Log the input string and check if it is empty
		String input = inputStringBuilder.toString();
		log.log("{}Get emmet expand input text at [{} - {}]: {}", LOG_TITLE, textStart, textEnd, input);
		if (input.trim().isEmpty()) {
			log.log("{}Fail to expand emmet, input text is empty", LOG_TITLE);
			return;
			
		}
		
		// STEP Invoke script engine to expand the input emmet expression, get the result string and new selection range
		String resultString = null;
		try {
			// SUBSTEP Invoke script engine
			log.log("{}Expand input, at [{}]: {}", LOG_TITLE, caretPosition, input);
			String output = (String) myInv.invokeFunction("job", input, caretPosition);
			log.log("{}Expand output: {}", LOG_TITLE, output);
			
			// SUBSTEP Parse the output string
			// 1.7
			/*
			if (result instanceof NativeObject) {
				outputData = (NativeObject) result;
			} else {
				throw new Exception("result is type: " + result.getClass().getName());
			}
			*/
			// 1.8
			Matcher matcher = EMMET_OUTPUT_PATTERN.matcher(output);
			if (matcher.find()) {
				resultString = matcher.group(1);
				newSelectionStart = Integer.parseInt(matcher.group(3));
				newSelectionStop = Integer.parseInt(matcher.group(4));
				
			}
			
		} catch (Exception e) {
			throw new RuntimeException(
					"Fail to expand emmet, unknown exception happened when invoking method with script engine: " 
							+ getBriefErrorInfo(e)
					, e
			);
			
		}
		
		// SUBSTEP Check if the result string is null
		if (resultString == null) {
			log.log("{}Fail to expand emmet, expanded result text is null", LOG_TITLE);
			return;
			
		}
		
		// SUBSTEP If the result string is not null, replace the text of document and move caret to new position
		try {
			// SUBSTEP Declare final variables
			final int textStartF = textStart;
			final int textEndF = textEnd;
			// 1.7
			// final String resultStringF = (String) outputData.get("text", null);
			// final int startSelection = ((Double) outputData.get("selectStart", null)).intValue();
			// final int stopSelection = ((Double) outputData.get("selectStop", null)).intValue();
			// 1.8
			final String resultStringF = resultString;
			final int newSelectionStartF = newSelectionStart;
			final int newSelectionStopF = newSelectionStop;
			
			// SUBSTEP Invoke IDEA API to replace the text and move the caret
			// NOTE To be compatible with JAVA 1.7 and earlier, use anonymous inner class instead of lambda expression
			ApplicationManager.getApplication().invokeLater(() -> CommandProcessor.getInstance().executeCommand(
					project
					, () -> ApplicationManager.getApplication().runWriteAction(() -> {
						document.replaceString(textStartF, textEndF, resultStringF);
						selectionModel.setSelection(newSelectionStartF, newSelectionStopF);
						caretModel.moveToOffset(newSelectionStopF);
						
					}
			), "DiskRead", null));
			
		} catch (Throwable e) {
			e.printStackTrace();
			
		}
	}
	
	// ===== ===== ===== ===== [Static Utility Method] ===== ===== ===== ===== //
	
	/** Initialize the static variable of logger */
	private static void initLogger() {
		if (log != null) { return; }
		log = BasicLeveledLoggerFactory.getLogger(MyEmmet.class, basicLogLevel);
		
	}
	
	/** Initialize the static variable of script engine */
	@SuppressWarnings("ConstantValue")
	private static void initScriptEngine() {
		if (myInv != null) { return; }
		ScriptEngineManager defaultFactory = new ScriptEngineManager();
		ScriptEngine engine = null;
		engine = (engine != null) ? engine : tryLoadScriptEngine(defaultFactory, ENGINE_MIME_TYPE, null);
		engine = (engine != null) ? engine : tryLoadScriptEngine(new ScriptEngineManager(null), ENGINE_MIME_TYPE, null);
		engine = (engine != null) ? engine : tryLoadScriptEngine(defaultFactory, null, ENGINE_NAME);
		engine = (engine != null) ? engine : tryLoadScriptEngineFromExternalGraalJs();
		engine = (engine != null) ? engine : tryLoadScriptEngineFromExternalNashorn();
		Objects.requireNonNull(
				engine
				, "Fail to load script engine by mime type [" 
						+ ENGINE_MIME_TYPE + "] or name [" 
						+ ENGINE_NAME + "] or external script engine from libraries [GraalJS] and [Nashorn]"
		);
		myInv = (Invocable) engine;
		
	}
	
	/** Load the script content of emmet into static script engine */
	private static void loadEmmetScript(ScriptEngine engine) {
		if (emmetScriptLoaded) { return; }
		try {
			engine.eval(readStringFrom(MyEmmet.class.getResourceAsStream(EMMET_SCRIPT_PATH)));
			emmetScriptLoaded = true;
			
		} catch (Exception e) {
			throw new RuntimeException("Fail to load emmet script", e);
			
		}
		
	}
	
	/** Try load script engine by MIME type or name with specified factory */
	private static ScriptEngine tryLoadScriptEngine(ScriptEngineManager factory, String engineMimeType, String engineName) {
		try {
			ScriptEngine engine;
			if (engineMimeType != null) {
				engine = factory.getEngineByMimeType(engineMimeType);
				log.log("{}{} to load script engine by mime type [{}]", LOG_TITLE, (engine != null) ? "Succeed" : "Fail", engineMimeType);
				
			} else {
				engine = factory.getEngineByName(engineName);
				log.log("{}{} to load script engine by name [{}]", LOG_TITLE, (engine != null) ? "Succeed" : "Fail", engineName);
				
			}
			return engine; 
			
		} catch (Exception e) {
			log.warn(
					"Fail to load script engine by mime type [{}] or name [{}], a null object will be return instead"
					, engineMimeType, engineName
			);
			return null;
			
		}
		
	}
	
	/** Try load script engine of external Graal JS implementation (in libraries) */
	private static ScriptEngine tryLoadScriptEngineFromExternalGraalJs() {
		try {
			ScriptEngine engine =  new GraalJSEngineFactory().getScriptEngine();
			log.log("{}{} to load script engine of specific extern library [Graal]", LOG_TITLE, "Succeed");
			return engine;
			
		} catch (Exception e) {
			log.warn("Fail to load script engine of Graal JS, a null object will be return instead");
			return null;
			
		}
		
	}
	
	/** Try load script engine of external Nashorn implementation (in libraries) */
	private static ScriptEngine tryLoadScriptEngineFromExternalNashorn() {
		try {
			ScriptEngine engine =  new NashornScriptEngineFactory().getScriptEngine();
			log.log("{}{} to load script engine of specific extern library [Nashorn]", LOG_TITLE, "Succeed");
			return engine;
			
		} catch (Exception e) {
			log.warn("Fail to load script engine of Graal JS, a null object will be return instead");
			return null;
			
		}
		
	}
	
	/** Read string from input stream */
	private static String readStringFrom(InputStream is) {
		// STEP Check input parameters
		if (is == null) { return null; }
		
		// STEP Declare variables
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		// STEP Read input stream
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if (br != null) {
				try {
					br.close();
					
				} catch (IOException e) {
					e.printStackTrace();
					
				}
				
			}
			
		}
		
		// Return the result
		return sb.toString();
		
	}
	
	/** Get brief error information from exception */
	private String getBriefErrorInfo(Exception e) {
		return "[" + e.getClass().getCanonicalName() + "]: " + (e.getMessage() == null ? "" : e.getMessage());
		
	}
	
	/** Check if position of caret is valid */
	private static boolean isCaretPositionValid(String fullText, int caretPosition) {
		boolean isValid;
		boolean caretAtEnd = (caretPosition == fullText.length());
		boolean caretAtStart = (caretPosition == 0);
		// BRANCH If caret sit at the end of document, there must be some text before it
		if (caretAtEnd) {
			isValid = true;
			
		// BRANCH If caret sit at the start of file, there must be no expression before it
		} else if (caretAtStart) {
			isValid = false;
			
		// BRANCH If caret sit at the mid of document, try to make further judgements
		} else {
			isValid = fullText.substring(caretPosition, caretPosition + 1).matches("\\s") 
					&& (fullText.substring(caretPosition - 1, caretPosition).matches("\\S"))
			;
			
		}
		return isValid;
		
	}
	
}
