/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.buildtools.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Checks if a class has illegal imports
 */
public class SdkIllegalImportCheck extends AbstractCheck {

    private final List<Pattern> illegalPackagesRegexps = new ArrayList<>();
    private final List<String> illegalPackages = new ArrayList<>();

    private Pattern classNameToCheck;
    private Pattern packageToCheck;

    private boolean containsIllegalImport = false;
    private boolean classMatched = false;
    private boolean classPackageMatched = false;

    public void setClassNameToCheck(String classNameToCheck) {
        this.classNameToCheck = CommonUtil.createPattern(classNameToCheck);
    }

    public void setPackageToCheck(String packageToCheckRegexp) {
        this.packageToCheck = CommonUtil.createPattern(packageToCheckRegexp);
    }

    public final void setIllegalPkgs(String... from) {
        illegalPackages.clear();
        illegalPackages.addAll(Arrays.asList(from));
        illegalPackagesRegexps.clear();
        for (String illegalPkg : illegalPackages) {
            illegalPackagesRegexps.add(CommonUtil.createPattern("^" + illegalPkg));
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF, TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        containsIllegalImport = false;
        classMatched = false;
        classPackageMatched = false;
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        // Set matched to true if no class package regex provided
        if (packageToCheck == null) {
            classPackageMatched = true;
        }

        if (containsIllegalImport && classMatched && classPackageMatched) {
            log(rootAST, "Illegal imports found " + illegalPackagesRegexps);
        }
    }

    @Override
    public void visitToken(DetailAST ast) {

        FullIdent importedPackage = null;
        FullIdent classPackage = null;

        switch (ast.getType()) {
            case TokenTypes.PACKAGE_DEF:
                classPackage = FullIdent.createFullIdent(ast.findFirstToken(TokenTypes.DOT));
                break;
            case TokenTypes.CLASS_DEF:
                String className = ast.findFirstToken(TokenTypes.IDENT).getText();
                if (classNameToCheck.matcher(className).matches()) {
                    classMatched = true;
                }
                break;
            case TokenTypes.IMPORT:
                importedPackage = FullIdent.createFullIdentBelow(ast);
                break;
            case TokenTypes.STATIC_IMPORT:
                importedPackage = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
                break;
        }

        if (packageToCheck != null
            && classPackage != null
            && packageToCheck.matcher(classPackage.getText()).matches()) {
            classPackageMatched = true;
        }

        if (importedPackage!= null && isIllegalImport(importedPackage.getText())) {
            containsIllegalImport = true;
        }
    }

    private boolean isIllegalImport(String importText) {
        for (Pattern pattern : illegalPackagesRegexps) {
            if (pattern.matcher(importText).matches()) {
                return true;
            }
        }
        return false;
    }
}
