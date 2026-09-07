const path = require('path');

module.exports = {
  testEnvironment: 'node',
  setupFilesAfterEnv: [path.resolve(__dirname, 'tests/setup-env.js')],
};