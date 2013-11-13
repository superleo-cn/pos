define(["jquery", "backbone"],

    function($, Backbone) {

        // Creates a new Backbone Model class object
        var Karyawan = Backbone.Model.extend({
            urlRoot :'/api/karyawan',
            // Model Constructor
            initialize: function() {
            },
            // Default values for all of the Model attributes
            defaults: { 
                 _id:'', 
                username:'',
                oldUsername:'',
                imgProfile:'',
                nama:'',
                jenisIdentitas:'',
                noIdentitas:'',
                tempatLahir:'',
                tglLahir:null,
                alamat:'',
                telepon:'',
                hp1:'',
                hp2:'',
                email:'',
                bank:'',
                noRekening:'',
                atasNama:'',
                cabang:'',
                namaCabang:'',
                status:''
            },

            // Gets called automatically by Backbone when the set and/or save methods are called (Add your own logic)
            validate: function(attrs) {

            }

        });

        // Returns the Model class
        return Karyawan;

    }

);
