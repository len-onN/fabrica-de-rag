import urllib.request
import json
import http.cookiejar

cookie_jar = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cookie_jar))

req2 = urllib.request.Request('http://localhost:8080/api/v1/me', headers={'X-Requested-With': 'XMLHttpRequest'})
try:
    res2 = opener.open(req2)
    print("Me:", res2.getcode())
except urllib.error.HTTPError as e:
    print("Me error:", e.code, e.read())
