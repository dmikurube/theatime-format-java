* google_check.xml: downloaded
    * https://github.com/checkstyle/checkstyle/blob/checkstyle-8.44/src/main/resources/google_checks.xml
* checkstyle.xml: customized from google_check.xml
    * Enable suppressions through /config/checkstyle/suppressions.xml
    * Enable suppressions with @SuppressWarnings
    * Indent with 4-column spaces
    * Limit columns to 180 characters
    * Reject unused imports.
