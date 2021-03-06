package com.cmakeplugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static com.cmakeplugin.annotator.CMakeAnnotatorUtils.*;
import static com.cmakeplugin.utils.CMakeProxyToJB.*;
import static com.cmakeplugin.utils.CMakePDC.*;

public class CMakeCLionAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (COMMAND_NAME_CLASS.isInstance(element)) {
      annotateCommand(element, holder);
    } else if (FUNCTION_CLASS.isInstance(element)) {
      annotateFunctionName(element, holder);
    } else if (MACRO_CLASS.isInstance(element)) {
      annotateMacrosName(element, holder);
    } else if (getCMakeArgumentClass().isInstance(element)) {
      if (!hasBracketArg(element)) {
        PsiElement cmakeLiteral = getCMakeLiteral(element);
        assert cmakeLiteral != null;
        if (hasQuotedArg(element)) {
          // Annotate Quoted argument
          annotateVarReferences(cmakeLiteral, holder);
        } else
        // Annotate Unquoted argument
        if (!(annotateLegacy(cmakeLiteral, holder)
            || annotatePredefinedVariable(cmakeLiteral, holder)
            || annotateProperty(cmakeLiteral, holder)
            || annotateOperator(cmakeLiteral, holder)
            || annotateVarDeclaration(cmakeLiteral, holder))) {
          annotateVarReferences(cmakeLiteral, holder);
          if (!(annotatePathURL(cmakeLiteral, holder))) {
            //              holder.createInfoAnnotation(cmakeLiteral, null)
            //                      .setTextAttributes(DefaultLanguageHighlighterColors.IDENTIFIER);
          }
        }
      }
    }
  }
}
