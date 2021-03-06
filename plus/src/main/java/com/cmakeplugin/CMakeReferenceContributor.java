package com.cmakeplugin;

import static com.cmakeplugin.utils.CMakeIFWHILEcheck.getInnerVars;
import static com.cmakeplugin.utils.CMakeProxyToJB.getCMakeLiteralClass;

import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CMakeReferenceContributor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    if (!CMakePDC.isCLION || !CMakeComponent.isCMakePlusActive) return;
    registrar.registerReferenceProvider(
        PlatformPatterns.psiElement(getCMakeLiteralClass()), //getCMakeArgumentClass()),
        new PsiReferenceProvider() {
          @NotNull
          @Override
          public PsiReference[] getReferencesByElement(
              @NotNull PsiElement element, @NotNull ProcessingContext context) {

            List<TextRange> innerVars = getInnerVars(element);
            if (innerVars.isEmpty()) return PsiReference.EMPTY_ARRAY;

            PsiReference[] result = new PsiReference[innerVars.size()];
            for (int i = 0; i < innerVars.size(); i++) {
              TextRange innerVar = innerVars.get(i);
              result[i] = new MyPsiPolyVariantReferenceBase<>(element, innerVar);
            }
            return result;
          }
        });
  }

  private static class MyPsiPolyVariantReferenceBase<T extends PsiElement>
      extends PsiPolyVariantReferenceBase {

    public MyPsiPolyVariantReferenceBase(@NotNull PsiElement element, TextRange range) {
      super(element, range);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
      return PsiElementResolveResult.createResults(
          CMakePSITreeSearch.findVariableDefinitions(getElement(), getValue()));
      //      return ResolveResult.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      return EMPTY_ARRAY;
    }
  }
}
