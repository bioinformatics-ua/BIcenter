define('Color', [], function () {
    function Color(r, g, b, a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    Color.prototype.getString = function () {
        return 'rgba('
            + this.r + ','
            + this.g + ','
            + this.b + ','
            + this.a
            + ')';
    };

    Color.prototype.toHex = function () {
        return "#" + this._componentToHex(this.r) + this._componentToHex(this.g) + this._componentToHex(this.b);
    };

    Color.prototype._componentToHex = function (c) {
        var hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    };

    Color.COLORS = Object.freeze({
        Neutral: new Color(255, 255, 255, 1),
        Red: new Color(192, 57, 43, 0.5),
        Green: new Color(39, 174, 96, 0.5),
        Blue: new Color(107, 185, 240, 0.5),
        Orange: new Color(243, 156, 18, 0.5),
        Pink: new Color(210, 82, 127, 0.5)
    });

    return Color;
});