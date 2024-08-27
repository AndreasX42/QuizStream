const fs = require('fs');

const filePath = './src/app/shared/environment/environment.ts';

// Read the environment.ts file
let content = fs.readFileSync(filePath, 'utf-8');

// Replace placeholders with actual environment variable values
content = content.replace('${API_URL}', process.env.API_URL || '');
content = content.replace('${API_PROV}', process.env.API_PROV || '');

// Write the updated content back to environment.ts
fs.writeFileSync(filePath, content);