/**
 * JFL 06/09/12
 */
templateEngine = TemplateEngine.get()

vertx.createHttpServer().requestHandler { req ->

    println 'uri->'+req.uri

    //def file = req.uri == "/" ? "index.html" : req.uri

    //req.response.sendFile "web/$file"
    def res = templateEngine.getTemplate('index')

    println '->'+ res

    req.response.end res

}.listen(8181, "localhost")
