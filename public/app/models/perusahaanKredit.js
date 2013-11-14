define(["jquery", "backbone"],

    function($, Backbone) {

        // Creates a new Backbone Model class object
        var PerusahaanKredit = Backbone.Model.extend({
            urlRoot :'/api/perusahaanKredit',
            // Model Constructor
            initialize: function() {
            },

            // Default values for all of the Model attributes
            defaults: {
                _id:'',
                inisial:'',
                namaPerusahaanKredit:'',
                kreditProtection:'',
                bungaPinjaman:[{
                    jenis:'',tahunMulai:'',tahunAkhir:'', dp:'',thn1:'',thn2:'',thn3:'',thn4:''
                }],
                bungaAsuransi:[{
                    jenis:'',thn1:'',thn2:'',thn3:'',thn4:''
                }],
                sukuBunga:{},
            },

            // Gets called automatically by Backbone when the set and/or save methods are called (Add your own logic)
            validate: function(attrs) {

            }

        });

        // Returns the Model class
        return PerusahaanKredit;

    }

);
