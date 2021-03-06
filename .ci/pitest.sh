#!/bin/bash
# Attention, there is no "-x" to avoid problems on Wercker
set -e

###############################
function checkPitestReport() {
  ignored=("$@")
  fail=0
  SEARCH_REGEXP="(span  class='survived'|class='uncovered'><pre>)"
  grep -irE "$SEARCH_REGEXP" target/pit-reports \
     | sed -E 's/.*\/([A-Za-z]+.java.html)/\1/' | LC_ALL=C sort > target/actual.txt
  printf "%s\n" "${ignored[@]}" | sed '/^$/d' | LC_ALL=C sort > target/ignored.txt
  if [ "$(diff --unified target/ignored.txt target/actual.txt)" != "" ] ; then
      fail=1
      echo "Actual:" ;
      grep -irE "$SEARCH_REGEXP" target/pit-reports \
         | sed -E 's/.*\/([A-Za-z]+.java.html)/\1/' | sort
      echo "Ignore:" ;
      printf '%s\n' "${ignored[@]}"
      echo "Diff:"
      diff --unified target/ignored.txt target/actual.txt | cat
  fi;
  if [ "$fail" -ne "0" ]; then
    echo "Difference between 'Actual' and 'Ignore' lists is detected, lists should be equal."
    echo "build will be failed."
  fi
  sleep 5s
  exit $fail
}
###############################

case $1 in

pitest-annotation|pitest-design|pitest-header|pitest-imports \
|pitest-metrics|pitest-modifier|pitest-naming \
|pitest-regexp|pitest-sizes|pitest-whitespace|pitest-ant \
|pitest-api|pitest-common|pitest-filters|pitest-main \
|pitest-packagenamesloader|pitest-tree-walker|pitest-utils \
|pitest-xpath|pitest-common-2|pitest-misc|pitest-blocks)
  mvn -e -P$1 clean test org.pitest:pitest-maven:mutationCoverage;
  declare -a ignoredItems=();
  checkPitestReport "${ignoredItems[@]}"
  ;;

pitest-coding)
  mvn -e -P$1 clean test org.pitest:pitest-maven:mutationCoverage;
  declare -a ignoredItems=(
  "EqualsAvoidNullCheck.java.html:<td class='covered'><pre><span  class='survived'>                    &#38;&#38; field.getColumnNo() + minimumSymbolsBetween &#60;= objCalledOn.getColumnNo()) {</span></pre></td></tr>"
  "HiddenFieldCheck.java.html:<td class='covered'><pre><span  class='survived'>            processVariable(ast);</span></pre></td></tr>"
  "MultipleVariableDeclarationsCheck.java.html:<td class='covered'><pre><span  class='survived'>                    &#38;&#38; newNode.getColumnNo() &#62; currentNode.getColumnNo()) {</span></pre></td></tr>"
  "MultipleVariableDeclarationsCheck.java.html:<td class='covered'><pre><span  class='survived'>            if (newNode.getLineNo() &#62; currentNode.getLineNo()</span></pre></td></tr>"
  "MultipleVariableDeclarationsCheck.java.html:<td class='covered'><pre><span  class='survived'>                || newNode.getLineNo() == currentNode.getLineNo()</span></pre></td></tr>"
  "RequireThisCheck.java.html:<td class='covered'><pre><span  class='survived'>                    &#38;&#38; ast1.getColumnNo() &#60; ast2.getColumnNo()) {</span></pre></td></tr>"
  "RequireThisCheck.java.html:<td class='covered'><pre><span  class='survived'>        final boolean methodNameInMethodCall = parentType == TokenTypes.DOT</span></pre></td></tr>"
  "UnnecessaryParenthesesCheck.java.html:<td class='covered'><pre><span  class='survived'>        if (type != TokenTypes.ASSIGN</span></pre></td></tr>"
  );
  checkPitestReport "${ignoredItems[@]}"
  ;;

pitest-indentation)
  mvn -e -P$1 clean test org.pitest:pitest-maven:mutationCoverage;
  declare -a ignoredItems=(
  "AbstractExpressionHandler.java.html:<td class='covered'><pre><span  class='survived'>            if (colNum == null || thisLineColumn &#60; colNum) {</span></pre></td></tr>"
  "AbstractExpressionHandler.java.html:<td class='covered'><pre><span  class='survived'>        if (currLine &#60; realStart) {</span></pre></td></tr>"
  "AbstractExpressionHandler.java.html:<td class='covered'><pre><span  class='survived'>            if (toTest.getColumnNo() &#60; first.getColumnNo()) {</span></pre></td></tr>"
  "ArrayInitHandler.java.html:<td class='covered'><pre><span  class='survived'>        if (firstChildPos &#62;= 0) {</span></pre></td></tr>"
  "BlockParentHandler.java.html:<td class='covered'><pre><span  class='survived'>                level.addAcceptedIndent(level.getFirstIndentLevel() + getLineWrappingIndent());</span></pre></td></tr>"
  "BlockParentHandler.java.html:<td class='covered'><pre><span  class='survived'>        return getIndentCheck().getLineWrappingIndentation();</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>            &#38;&#38; root.getFirstChild().getFirstChild().getFirstChild().getNextSibling() != null;</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>                if (isUsingOfObjectReferenceToInvokeMethod(blockBody)) {</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        if (isUsingOfObjectReferenceToInvokeMethod(root)) {</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>            if (root.getFirstChild().getType() == TokenTypes.LITERAL_NEW) {</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        if (root.getLineNo() &#62;= comment.getLineNo()) {</span></pre></td></tr>"
  "CommentsIndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        return root.getFirstChild().getFirstChild().getFirstChild() != null</span></pre></td></tr>"
  "HandlerFactory.java.html:<td class='covered'><pre><span  class='survived'>        createdHandlers.clear();</span></pre></td></tr>"
  "HandlerFactory.java.html:<td class='covered'><pre><span  class='survived'>        register(TokenTypes.INDEX_OP, IndexHandler.class);</span></pre></td></tr>"
  "IndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        handlerFactory.clearCreatedHandlers();</span></pre></td></tr>"
  "IndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        handlers.clear();</span></pre></td></tr>"
  "IndentationCheck.java.html:<td class='covered'><pre><span  class='survived'>        primordialHandler.checkIndentation();</span></pre></td></tr>"
  "IndentLevel.java.html:<td class='covered'><pre><span  class='survived'>            for (int i = levels.nextSetBit(0); i &#62;= 0;</span></pre></td></tr>"
  "MethodDefHandler.java.html:<td class='covered'><pre><span  class='survived'>            if (node.getLineNo() &#60; lineStart) {</span></pre></td></tr>"
  "MethodDefHandler.java.html:<td class='covered'><pre><span  class='survived'>            if (node.getType() == TokenTypes.ANNOTATION) {</span></pre></td></tr>"
  "NewHandler.java.html:<td class='covered'><pre><span  class='survived'>        return false;</span></pre></td></tr>"
  "SwitchHandler.java.html:<td class='covered'><pre><span  class='survived'>        checkExpressionSubtree(</span></pre></td></tr>"
  "SwitchHandler.java.html:<td class='covered'><pre><span  class='survived'>        checkSwitchExpr();</span></pre></td></tr>"
  "SynchronizedHandler.java.html:<td class='covered'><pre><span  class='survived'>        checkExpressionSubtree(syncAst, expected, false, false);</span></pre></td></tr>"
  "SynchronizedHandler.java.html:<td class='covered'><pre><span  class='survived'>            checkSynchronizedExpr();</span></pre></td></tr>"
  "SynchronizedHandler.java.html:<td class='covered'><pre><span  class='survived'>            checkWrappingIndentation(getMainAst(),</span></pre></td></tr>"
  "TryHandler.java.html:<td class='covered'><pre><span  class='survived'>            checkTryResParen(getTryResLparen(), &#34;lparen&#34;);</span></pre></td></tr>"
  );
  checkPitestReport "${ignoredItems[@]}"
  ;;

pitest-javadoc)
  mvn -e -P$1 clean test org.pitest:pitest-maven:mutationCoverage;
  declare -a ignoredItems=(
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>            Arrays.sort(acceptableJavadocTokens);</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>            Arrays.sort(defaultJavadocTokens);</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        beginJavadocTree(root);</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        finishJavadocTree(root);</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        javadocTokens.clear();</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        TREE_CACHE.get().clear();</span></pre></td></tr>"
  "AbstractJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        TREE_CACHE.get().clear();</span></pre></td></tr>"
  "AbstractTypeAwareCheck.java.html:<td class='covered'><pre><span  class='survived'>        if (!currentClassName.isEmpty()) {</span></pre></td></tr>"
  "AbstractTypeAwareCheck.java.html:<td class='covered'><pre><span  class='survived'>            if (dotIdx == -1) {</span></pre></td></tr>"
  "AbstractTypeAwareCheck.java.html:<td class='covered'><pre><span  class='survived'>        imports.clear();</span></pre></td></tr>"
  "AbstractTypeAwareCheck.java.html:<td class='covered'><pre><span  class='survived'>        typeParams.clear();</span></pre></td></tr>"
  "JavadocMethodCheck.java.html:<td class='covered'><pre><span  class='survived'>        while (remIndex &#60; lines.length) {</span></pre></td></tr>"
  "JavadocMethodCheck.java.html:<td class='covered'><pre><span  class='survived'>        while (remIndex &#60; lines.length) {</span></pre></td></tr>"
  "JavadocPackageCheck.java.html:<td class='covered'><pre><span  class='survived'>        directoriesChecked.clear();</span></pre></td></tr>"
  "JavadocPackageCheck.java.html:<td class='covered'><pre><span  class='survived'>        super.beginProcessing(charset);</span></pre></td></tr>"
  "JavadocTagInfo.java.html:<td class='covered'><pre><span  class='survived'>            .collect(Collectors.toMap(JavadocTagInfo::getName, tagName -&#62; tagName)));</span></pre></td></tr>"
  "JavadocTagInfo.java.html:<td class='covered'><pre><span  class='survived'>            .collect(Collectors.toMap(JavadocTagInfo::getText, tagText -&#62; tagText)));</span></pre></td></tr>"
  "JavadocTypeCheck.java.html:<td class='covered'><pre><span  class='survived'>                    tagCount++;</span></pre></td></tr>"
  "SummaryJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>        for (int i = 0; !found &#38;&#38; i &#60; children.length - 1; i++) {</span></pre></td></tr>"
  "SummaryJavadocCheck.java.html:<td class='covered'><pre><span  class='survived'>            if (child.getType() != JavadocTokenTypes.JAVADOC_INLINE_TAG</span></pre></td></tr>"
  "TagParser.java.html:<td class='covered'><pre><span  class='survived'>                while (column &#60; currentLine.length()</span></pre></td></tr>"
  "WriteTagCheck.java.html:<td class='covered'><pre><span  class='survived'>                    tagCount += 1;</span></pre></td></tr>"
  );
  checkPitestReport "${ignoredItems[@]}"
  ;;

# pitesttyle-gui)
#   mvn -e -P$1 clean test org.pitest:pitest-maven:mutationCoverage;
#   # post validation is skipped, we do not test gui throughly
#   ;;

*)
  echo "Unexpected argument: $1"
  sleep 5s
  false
  ;;

esac



