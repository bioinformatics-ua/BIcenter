define(['handlebars.runtime','md5'], function (Handlebars,md5) {
    Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {
        console.log(v1,operator,v2);
        switch (operator) {
            case '==':
                return (v1 == v2) ? options.fn(this) : options.inverse(this);
            case '===':
                return (v1 === v2) ? options.fn(this) : options.inverse(this);
            case '!=':
                return (v1 != v2) ? options.fn(this) : options.inverse(this);
            case '!==':
                return (v1 !== v2) ? options.fn(this) : options.inverse(this);
            case '<':
                return (v1 < v2) ? options.fn(this) : options.inverse(this);
            case '<=':
                return (v1 <= v2) ? options.fn(this) : options.inverse(this);
            case '>':
                return (v1 > v2) ? options.fn(this) : options.inverse(this);
            case '>=':
                return (v1 >= v2) ? options.fn(this) : options.inverse(this);
            case '&&':
                return (v1 && v2) ? options.fn(this) : options.inverse(this);
            case '||':
                return (v1 || v2) ? options.fn(this) : options.inverse(this);
            case 'contains':
                if(v1==null) return false;
                return (JSON.parse(v1).includes(v2)) ? options.fn(this) : options.inverse(this);
            case 'in':
                return (v2.indexOf(v1) !== -1) ? options.fn(this) : options.inverse(this);;
            default:
                return options.inverse(this);
        }
    });

    Handlebars.registerHelper('md5', function(email){
        return md5(email);
    });
});