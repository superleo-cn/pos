define(["jquery", "backbone"],

    function($, Backbone) {

        // Creates a new Backbone Model class object
        var Quote = Backbone.Model.extend({
            urlRoot :'/api/quote',
            // Model Constructor
            initialize: function() {
            },

            // Default values for all of the Model attributes
            defaults: {
                _id:'',
                customer:{alamat:{jalan:'',rt:'',rw:''},telepon:{hp1:'',hp2:'',rmh:'',kantor:''}},
                imgProfile :'',
                kendaraanRoda:'',
                jenisKendaraan:'',
                namaKendaraan:'',
                tahunBuat:'',
                hargaOTR:'',
                perusahaanKredit:{},
                lamaPinjaman:1,
                sukuBunga:'',
                asstTlo:'',
                percentDP:'',
                cashBack:'',
                carDepreciation:'',
                pengurangTerakhir:'',
                pengurangDP:'',
                provisi:800000,
                bungaDibayar:'',
                admInsurance :5000000,
                coverage:'',
                kreditProtection :0,
                bungaAsuransi:'',
                bungaPinjaman:'',
                status:''
            },

            // Gets called automatically by Backbone when the set and/or save methods are called (Add your own logic)
            validate: function(attrs) {

            }

        });

        // Returns the Model class
        return Quote;

    }

);
