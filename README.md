![Build](https://github.com/edejin/Emmet-Everywhere/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/7450-emmet-everywhere.svg)](https://plugins.jetbrains.com/plugin/7450-emmet-everywhere)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/7450-emmet-everywhere.svg)](https://plugins.jetbrains.com/plugin/7450-emmet-everywhere)


- Old version1 https://github.com/edejin/EmmetEverywhere
- Old version2 https://github.com/niontrix/Emmet-Everywhere
- Forked from https://github.com/niontrix/Emmet-Everywhere

<!-- Plugin description -->

## Change Notes

### 1.2.7 - 2023-03-29

- Fix the problem of plugin with `Old version2` that fail to load script engine since IDEA running with JDK17.
	- In those versions of IDEA, the script engine `Nashorn` is not defaultly embedded in JDK.
	- This new version of plugin will try to load script engine from dependencies to solve this.
	- So the packages will be a little bit bigger (contains the libraries of `Graal JS` and `Nashorn`).
- Add selection support for expanding operation.
	- Now the selection instead of characters at left side of caret will be expanded if you do select. 

EmmetEverywhere Plugin for IntelliJ IDEA
========================================

Use HTML Emmet anywhere.

For example in Google Closure Template (*.soy files).

Just type your code and press `CTRL+ALT+]`

Here’s an example: this abbreviation

`#page>div.logo+ul#navigation>li*5>a{Item $}`

...can be transformed into

```html
<div id="page">
  <div class="logo"></div>
  <ul id="navigation">
    <li><a href="">Item 1</a></li>
    <li><a href="">Item 2</a></li>
    <li><a href="">Item 3</a></li>
    <li><a href="">Item 4</a></li>
    <li><a href="">Item 5</a></li>
  </ul>
</div>
```

More information about Emmet:

http://emmet.io/
<!-- Plugin description end -->
