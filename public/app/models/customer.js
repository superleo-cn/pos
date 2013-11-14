define(["jquery", "backbone"],

    function($, Backbone) {

        // Creates a new Backbone Model class object
        var Customer = Backbone.Model.extend({
            urlRoot :'/api/customer',
            // Model Constructor
            initialize: function() {
            },

            // Default values for all of the Model attributes
            defaults: {
                _id:'',
                noIdentitas : '',
                tipeIdentitas :'',
                imgProfile :'',
                gender:'',
                kantor:'',
                alamat: {
                    jalan :'',
                    rt:'',
                    rw:'',
                    lurah:'',
                    camat:'',
                    kotamadya:'',
                    kodePos:'',
                    kota:''
                },
                telepon : {
                    rmh:'',
                    kantor:'',
                    hp1:'',
                    hp2:''
                },
                namaCustomer : '',
                kantor: '',
                email:'',
                bank : {
                    nama:'',
                    noRekening:'',
                    atasNama:''
                }

            },

            // Gets called automatically by Backbone when the set and/or save methods are called (Add your own logic)
            validate: function(attrs) {

            }

        });

        // Returns the Model class
        return Customer;

    }

);
