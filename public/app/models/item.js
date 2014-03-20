define(["jquery", "backbone"],

    function($, Backbone) {

        // Creates a new Backbone Model class object
        var Item = Backbone.Model.extend({
            urlRoot :'/masterItem',
            // Model Constructor
            initialize: function() {
            },

            // Default values for all of the Model attributes
            defaults: {
                id:'',
                sn:'',
                name:'',
                nameZh:'',
                picture:'',
                costPrice:0,
                retailPrice:0,
                type:'',
                status:'',
                flag:'',
                position:'',
                barCode:'',
                attachment:null
            },

            // Gets called automatically by Backbone when the set and/or save methods are called (Add your own logic)
            validate: function(attrs) {

            },
            readFile: function(file,errorCb,successCb) {
                var reader = new FileReader();
                // closure to capture the file information.
                reader.onload = (function(attachment,tha,errorCb,successCb) {
                    return function(e) {
                        //set model
                        $.post('/masterItem/upload', {attachment:e.target.result},function(data){
                            console.log(data);
                            if(data.error){
                                alert(data.error);
                                errorCb();
                            }
                            else {
                                alert("Successfully insert "+data.size+" items");
                                successCb();
                            }
                            
                        });

                    };
                })(file,this,errorCb,successCb);

                // Read in the image file as a data URL.
                reader.readAsDataURL(file);
            }   

        });

        // Returns the Model class
        return Item;

    }

);
