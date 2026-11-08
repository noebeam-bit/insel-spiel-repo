// Wird beim APK-Bau in index.html eingefügt. Im Browser speichert „Als Datei speichern“ über einen
// Download-Link; den kennt die Android-App nicht, darum geht die Datei hier an das native Plugin.
(function () {
  var orig = HTMLAnchorElement.prototype.click;
  HTMLAnchorElement.prototype.click = function () {
    var P = window.Capacitor && window.Capacitor.Plugins && window.Capacitor.Plugins.InselDatei;
    if (P && this.download && String(this.href).indexOf('blob:') === 0) {
      var name = this.download;
      fetch(this.href)
        .then(function (r) { return r.text(); })
        .then(function (t) { return P.save({ filename: name, data: t }); })
        .catch(function () {});
      return;
    }
    return orig.call(this);
  };
})();
