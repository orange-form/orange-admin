module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'semi': ['off', 'always'],
    'prefer-promise-reject-errors': ['error', { 'allowEmptyReject': true }],
    'no-trailing-spaces': ['error', { 'skipBlankLines': true }],
    'prefer-const': ['off'],
    'quote-props': ['off'],
    'object-curly-spacing': ['off'],
    'dot-notation': ['off'],
    'lines-between-class-members': ['off'],
    // 'no-undef': ['off', 'always'],
    // 'no-unused-vars': ['off', 'always'],
    'no-new-func': ['off', 'always']
  },
  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        jest: true
      }
    }
  ]
}
