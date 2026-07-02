const request = require('http').request({
  hostname: 'localhost',
  port: 8080,
  path: '/api/v1/bootstrap',
  method: 'POST',
  headers: { 'Content-Type': 'application/json' }
}, res => {
  let data = '';
  res.on('data', chunk => data += chunk);
  res.on('end', () => console.log('Status:', res.statusCode, '\nBody:', data));
});
request.write(JSON.stringify({
  displayName: 'Test',
  email: 'test@test.com',
  password: 'Password123!',
  workspaceName: 'Test',
  workspacePurpose: 'evaluation'
}));
request.end();
