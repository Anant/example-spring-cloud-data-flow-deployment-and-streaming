import groovy.json.JsonSlurper
println('new version')
println("Groovy processing payload '" + payload + "'");
jsonStr = new String(payload)
println(jsonStr)
def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText(jsonStr)
println('title is')
println(object.title)
if (payload.contains('us')) {
    return "us"
}
else {
    return "euro"