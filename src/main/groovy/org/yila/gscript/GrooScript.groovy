package org.yila.gscript

/**
 * JFL 09/11/12
 */
class GrooScript {

    def static GsConverter converter

    def static GsConverter getConverter() {
        if (!converter) {
            converter = new GsConverter()
        }
        converter
    }

    def static convert(String text) {
        if (text) {
            return getConverter().toJs(text)
        } else {
            throw new Exception("Nothing to Convert.")
        }
    }
}
