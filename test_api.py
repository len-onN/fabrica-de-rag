import urllib.request
import json
import http.cookiejar

cookie_jar = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cookie_jar))

# Bootstrap
data = json.dumps({'email':'pedro@pedro.com','password':'Password123!','displayName':'Pedro','workspaceName':'Test','workspacePurpose':'test'}).encode('utf-8')
req = urllib.request.Request('http://localhost:8080/api/v1/bootstrap', data=data, headers={'Content-Type': 'application/json'})
try:
    res = opener.open(req)
    print("Bootstrap:", res.getcode())
    print("Cookies after bootstrap:", [c.name for c in cookie_jar])
except urllib.error.HTTPError as e:
    print("Bootstrap error:", e.code, e.read())

# Me
req2 = urllib.request.Request('http://localhost:8080/api/v1/me')
try:
    res2 = opener.open(req2)
    print("Me:", res2.getcode())
    print("Response:", res2.read().decode('utf-8'))
except urllib.error.HTTPError as e:
    print("Me error:", e.code, e.read())
