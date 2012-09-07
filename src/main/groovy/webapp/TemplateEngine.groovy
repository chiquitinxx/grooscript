/**
 * JFL 08/09/12
 */
class TemplateEngine {

    def static final FOLDER_TEMPLATES = 'pages'
    def static final TEMPLATES_EXTENSION = '.groovy'
    def static final MAIN_TEMPLATE = 'main'
    def static final BODY_TAG_IN_MAIN = '$BODY$'

    def static final HEAD1 = '<?xml version="1.0" encoding="UTF-8"?>'
    def static final HEAD2 = '<!DOCTYPE html>'

    def static TemplateEngine me;
    def static get() {
        if (!me) {
            me = new TemplateEngine()
        }
        return me
    }

    TemplateEngine() {

    }

    def getTemplate(String name) {

        def String nameFile = name
        if (!nameFile.endsWith(TEMPLATES_EXTENSION)) {
            nameFile += TEMPLATES_EXTENSION
        }
        def file = new File(FOLDER_TEMPLATES+System.getProperty('file.separator')+nameFile)

        if (!file || !file.isFile() || !file.exists()) {
            throw new Exception("TemplateEngine template $name doesn't exist.")
        }
        def fileMain = new File(FOLDER_TEMPLATES+System.getProperty('file.separator')+MAIN_TEMPLATE+TEMPLATES_EXTENSION)

        if (!fileMain || !fileMain.isFile() || !fileMain.exists()) {
            throw new Exception("TemplateEngine template Main doesn't exist.")
        }

        def String result = getComposedTemplate(fileMain,file)
        return result
    }

    def getComposedTemplate(File fileMain,File file) {

        def textMain = fileMain.text
        def pos = textMain.indexOf(BODY_TAG_IN_MAIN)
        textMain = textMain.substring(0,pos-1) + file.text + textMain.substring(pos+BODY_TAG_IN_MAIN.length())

        def writer = new StringWriter()
        def html = new groovy.xml.MarkupBuilder(writer)

        def binding = new Binding()
        binding.html = html
        //binding.pageTitle = 'GScript'
        def shell = new GroovyShell(binding)
        def result = shell.evaluate("{ -> "+textMain+'}')

        result()

        return HEAD1 + '\n' + HEAD2 + '\n' + writer

    }
}
