/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#0066cc',
        'primary-focus': '#0071e3',
        'primary-on-dark': '#2997ff',
        canvas: '#ffffff',
        parchment: '#f5f5f7',
        ink: '#1d1d1f',
        'ink-muted': '#7a7a7a',
        'surface-tile': '#272729',
        hairline: '#e0e0e0',
      },
      fontFamily: {
        sans: ['SF Pro Text', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'sans-serif'],
        display: ['SF Pro Display', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'sans-serif'],
      },
      borderRadius: {
        pill: '9999px',
        card: '18px',
      },
    },
  },
  plugins: [],
}
