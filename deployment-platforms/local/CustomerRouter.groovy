import groovy.json.JsonSlurper
jsonStr = new String(payload)
println(jsonStr)
def jsonSlurper = new JsonSlurper()
def customer = jsonSlurper.parseText(jsonStr) 

println(customer.region)
if (customer.region =='us') {
    return "customerus"
}
else if (customer.region == 'euro') {
    return "customereuro"
}
else if (customer.region == 'asia') {
	return "customerasia"
}else{
	return "customerother"
}