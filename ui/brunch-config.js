module.exports = {
  paths: {
    public: 'target/public',
    watched: ['src', 'target/js']
  },

  conventions: {
    assets: /^(src\/static|target\/js\/out)/,
    vendor: /^(node_modules|target\/js\/(?!out\/))/
  },

  files: {
    javascripts: {joinTo: 'assets/app.js'},
    stylesheets: {joinTo: 'assets/app.css'}
  },

  plugins: {
    sass: {
      options: {
        includePaths: ['node_modules/bootstrap/scss']
      }
    },

    svgsprite: {
      mode: {
        symbol: {
          dest: './target/public/assets',
          sprite: 'icons.svg'
        }
      }
    }
  }
};
