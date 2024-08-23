const fs = require('fs');

const filePath = 'src/app/shared/environment/environment.ts';

// Read the environment.ts file
let content = fs.readFileSync(filePath, 'utf-8');

// Replace placeholders with actual environment variable values
content = content.replace('${API_HOST}', process.env.API_HOST || 'localhost');
content = content.replace('${API_PORT}', process.env.API_PORT || '8080');

// Write the updated content back to environment.ts
fs.writeFileSync(filePath, content);