package org.grooscript

/**
 * JFL 09/11/12
 */
class GrooScript {

    def private static GsConverter converter
    def private static String ownClassPath

    /**
     * Get a GsConverter singleton
     * @return
     */
    def static GsConverter getConverter() {
        if (!converter) {
            converter = new GsConverter()
        }
        converter
    }

    /**
     * Convert a piece of groovy code to javascript
     * @param String text groovy code
     * @return String javascript result code
     * @throws Exception If conversion fails or text is null
     */
    def static convert(String text) {
        if (text) {
            return getConverter().toJs(text)
        } else {
            throw new Exception("Nothing to Convert.")
        }
    }

    /**
     * Converts from a source to destination, groovy files to javascript files
     * Result files will be .js with same name that groovy file
     * @param source path to directory with groovy files, or a groovy file path. Not recursive
     * @param destination directory of .js files
     * @throws Exception something fails
     */
    def static convert(String source, String destination) {
        if (source && destination) {
            File fSource = new File(source)
            File fDestination = new File(destination)

            if (fSource.exists() && fDestination.exists() && fDestination.isDirectory()) {
                if (!fSource.isDirectory()) {
                    fileConvert(fSource,fDestination)
                } else {
                    fSource.eachFile { file ->
                        if (file.isFile()) {
                            fileConvert(file,fDestination)
                        }
                    }
                }
            } else {
                throw new Exception("Source and destination must exist, and destination must be a directory.")
            }

        } else {
            throw new Exception("Have to define source and destination.")
        }
    }

    /**
     * Set the dir where all your groovy starts, the mainSource ( src/main/groovy, src/groovy, ..)
     * @param dir
     * @return
     */
    def static setOwnClassPath(String dir) {
        ownClassPath = dir
    }

    def private static fileConvert(File source,File destination) {
        if (source.isFile() && source.name.endsWith('.groovy')) {
            //println 'Name file->'+source.name
            def name = source.name.split(/\./)[0]
            def jsResult = getConverter().toJs(source.text,ownClassPath)

            //println 'Result file->'+destination.path+System.getProperty('file.separator')+name+'.js'
            def newFile = new File(destination.path+System.getProperty('file.separator')+name+'.js')
            if (newFile.exists()) {
                newFile.delete()
            }
            newFile.write(jsResult)
            println 'Converted file: '+newFile.name
        }
    }
}
