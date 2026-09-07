process.env.JWT_SECRET = 'test-jwt-secret-key-32-chars-minimum-length!';

module.exports = {
  // This file is loaded by Jest before running tests
  // It sets a test-only JWT_SECRET so backend tests can run without
  // requiring production .env secrets
};