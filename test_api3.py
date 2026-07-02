import urllib.request
import json
import http.cookiejar

# Bootstrap
data = json.dumps({'email':'pedro2@pedro.com','password':'Password123!','displayName':'Pedro','workspaceName':'Test','workspacePurpose':'test'}).encode('utf-8')
req = urllib.request.Request('http://localhost:8080/api/v1/bootstrap', data=data, headers={'Content-Type': 'application/json'})

res = urllib.request.urlopen(req)
print("Bootstrap:", res.getcode())
set_cookie = res.getheader('Set-Cookie')
print("Set-Cookie header:", set_cookie)

# Extract rag_session from the Set-Cookie headers
cookies = res.headers.get_all('Set-Cookie')
rag_session = None
for c in cookies:
    if c.startswith('rag_session='):
        rag_session = c.split(';')[0]

print("Found rag_session:", rag_session)

if rag_session:
    req2 = urllib.request.Request('http://localhost:8080/api/v1/me', headers={'Cookie': rag_session})
    try:
        res2 = urllib.request.urlopen(req2)
        print("Me:", res2.getcode())
        print(res2.read())
    except urllib.error.HTTPError as e:
        print("Me error:", e.code, e.read())
