/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/resources/**/*.{html,js}"],
  theme: {
    extend: {backgroundImage: {
        'hero-pattern': "url('/images/callimage.jpg')",
      },},
  },
  plugins: [],
}