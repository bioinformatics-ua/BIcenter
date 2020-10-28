define('DataSourceView', ['Modal'], function (Modal) {

    var DataSourceView = function (controller) {
        Modal.call(this, controller, 'dataSourceModal', 'Edit Data Source');
    };

    // Inheritance from super class
    DataSourceView.prototype = Object.create(Modal.prototype);
    var _super_ = Modal.prototype;

    DataSourceView.prototype.clear = function () {

    };

    DataSourceView.prototype.show = function (dataSource,institutions) {
        // Clear modal before show
        this.clear();

        // Show modal
        _super_.show.call(this);

        var connectionTypes = [
            {"value":"AS/400","name":"AS/400"},
            {"value":"DERBY","name":"Apache Derby"},
            {"value":"INTERBASE","name":"Borland Interbase"},
            {"value":"INFINIDB","name":"Calpont InfiniDB"},
            {"value":"EXASOL4","name":"Exasol 4"},
            {"value":"EXTENDB","name":"ExtenDB"},
            {"value":"FIREBIRD","name":"Firebird SQL"},
            {"value":"GENERIC","name":"Generic database"},
            {"value":"GREENPLUM","name":"Greenplum"},
            {"value":"SQLBASE","name":"Gupta SQL Base"},
            {"value":"H2","name":"H2"},
            {"value":"HYPERSONIC","name":"Hypersonic"},
            {"value":"DB2","name":"IBM DB2"},
            {"value":"INFOBRIGHT","name":"Infobright"},
            {"value":"INFORMIX","name":"Informix"},
            {"value":"INGRES","name":"Ingres"},
            {"value":"VECTORWISE","name":"Ingres VectorWise"},
            {"value":"CACHE","name":"Intersystems Cache"},
            {"value":"KINGBASEES","name":"KingbaseES"},
            {"value":"LucidDB","name":"LucidDB"},
            {"value":"MSACCESS","name":"MS Access"},
            {"value":"MSSQL","name":"MS SQL Server"},
            {"value":"MSSQLNATIVE","name":"MS SQL Server (Native)"},
            {"value":"SAPDB","name":"MaxDB (SAP DB)"},
            {"value":"MONETDB","name":"MonetDB"},
            {"value":"MYSQL","name":"MySQL"},
            {"value":"MONDRIAN","name":"Native Mondrian"},
            {"value":"NEOVIEW","name":"Neoview"},
            {"value":"NETEZZA","name":"Netezza"},
            {"value":"ORACLE","name":"Oracle"},
            {"value":"ORACLERDB","name":"Oracle RDB"},
            {"value":"POSTGRESQL","name":"PostgreSQL"},
            {"value":"REDSHIFT","name":"Redshift"},
            {"value":"REMEDY-AR-SYSTEM","name":"Remedy Action Request System"},
            {"value":"SAPR3","name":"SAP ERP System"},
            {"value":"SQLITE","name":"SQLite"},
            {"value":"SYBASE","name":"Sybase"},
            {"value":"SYBASEIQ","name":"SybaseIQ"},
            {"value":"TERADATA","name":"Teradata"},
            {"value":"UNIVERSE","name":"UniVerse database"},
            {"value":"VERTICA","name":"Vertica"},
            {"value":"VERTICA5","name":"Vertica 5+"},
            {"value":"DBASE","name":"dBase III, IV or 5"}
        ];
        var accessTypes = [
            {"value":0,"name":"Native (JDBC)"},
            {"value":1,"name":"ODBC"},
            {"value":4,"name":"JNDI"}
        ];

        var html = JST['dataSourceModalContent']({
            dataSource: dataSource,
            institutions: institutions,
            connectionTypes: connectionTypes,
            accessTypes: accessTypes
        });

        this.$elements.insideContainer.html(html);
        this._loadViewComponents();
    };

    return DataSourceView;
});