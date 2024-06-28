module.exports = {
    root: true,
    env: {
        browser: true,
        es2021: true,
    },
    extends: ["plugin:prettier/recommended"],
    parserOptions: {
        ecmaFeatures: {
            jsx: true,
        },
        ecmaVersion: "latest",
        sourceType: "module",
    },
    plugins: ["react"],
    rules: {
        "prettier/prettier": ["error", { endOfLine: "auto" }],

        // eslint와 같이 사용하는 부분에 있어 내부적 이슈로 인한 off
        "arrow-body-style": "off",
        "prefer-arrow-callback": "off",
    },
};
