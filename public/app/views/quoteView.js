define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/quoteList.html',
    'text!../templates/quoteForm.html',
    'text!../templates/quoteForm.html',
    'text!../templates/quotePrintSection.html',
    '../models/quote'
], function($, _, Backbone, listTemplate,formTemplate,formEditTemplate,print1SectionTemplate,Model){

    var QuoteView = Backbone.View.extend({
        el: $('#container'),
        events: {
            "click .save"   : "save",
            "click .saveKontrak"   : "save",
            "click .print"   : "print",
            "click .cancel"   : "cancel",
            "click #tambahCustomer"   : "tambahCustomer",
            "change #lamaPinjaman": "updateSukuBunga",
            "change #tahunBuat": "updatePerusahaanKredit",
        },
        print:function(){
            window.print();
         },
          updatePerusahaanKredit:function(){
           
                if(this.perusahaanKredit) {
                  
                    $('#perusahaanKredit').trigger('change');
                
                }
        },
        updateSukuBunga:function(){
            var lamaPinjaman = this.$el.find('#lamaPinjaman').val()*1;
            
            if(lamaPinjaman && this.perusahaanKredit) {
              
                $('#bungaPinjaman').trigger('change');

                if(this.$el.find('#kreditProtectionSection:visible'))
                  this.$el.find('#kreditProtection').val((0.3*lamaPinjaman).toFixed(1));
                else
                  this.$el.find('#kreditProtection').val("0");
                
                //this.$el.find('#sukuBunga').val(tmp);
            }
        },
        initialize:function(options){
            console.log('initialize quote view ');
            this.$el.undelegate();

            var that = this;
            this.model = new Model();

            this.listenTo(this.model,'error', function(model,e) {
                var err;
                try {
                    var response = JSON.parse(e.responseText);
                    err = response.err;
                }catch(ex) {
                    err =  e.responseText;
                }
                var alertError = $('#alertError').clone();
                $('span',alertError).html('Terjadi masalah : '+err);
                alertError.show().appendTo('.formMessage');
                that.quoteForm.find('.save').button('reset');


            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');
                if(!that.options.id)
                   this.model.set(new Model().toJSON());
                that.render('form',function(){
                    var alertInfo = $('#alertInfo').clone();
                    $('span',alertInfo).html('Sukses menyimpan Proposal');
                    alertInfo.show().appendTo('.formMessage');
                    that.quoteForm.find('.save').button('reset');
                });

            });

        },
        save:function(e) {
            this.quoteForm = this.$el.find('#quoteForm');
            console.log(this.quoteForm.serializeObject());

            if(this.quoteForm.valid()) {
                var status = e.target.textContent;

                this.model.set(this.quoteForm.serializeObject());
                this.model.set('perusahaanKredit',this.perusahaanKredit);
                this.model.set('customer',this.customer);
                this.model.set('status',status);
                this.quoteForm.find('.save').button('loading');
                this.model.save();
            }
        },
        tambahCustomer:function(e) {

            app.router.addCustomerModal();
        },
        cancel:function() {
            app.router.navigate('/quote',{trigger:true});
        },
        calculateQuote:function(f) {
            var cashBack = $('#cashBackMask').inputmask('unmaskedvalue')*1;
            var carDepreciation = $('#carDepreciationMask').inputmask('unmaskedvalue')*1;
            var pengurangTerakhir = $('#pengurangTerakhirMask').inputmask('unmaskedvalue')*1;
            var pengurangDP = $('#pengurangDPMask').inputmask('unmaskedvalue')*1;
            
            var provisi = $('#provisiMask').inputmask('unmaskedvalue')*1;
            var admInsurance = $('#admInsuranceMask').inputmask('unmaskedvalue')*1;
            var hargaOTR = $('#hargaOTRMask').inputmask('unmaskedvalue')*1;
                

            $('#cashBack').val(cashBack);
            $('#carDepreciation').val(carDepreciation);
            $('#pengurangTerakhir').val(pengurangTerakhir);
            $('#provisi').val(provisi);

            var loanPrincipal = 0;
            loanPrincipal = hargaOTR-(f.percentDP*1/100*hargaOTR)-pengurangDP-loanPrincipal;
            var kreditProtection = 0;
            if(!that.perusahaanKredit.kreditProtection)
                f.kreditProtection=0;
            
            if(f.kreditProtection!=0) 
              kreditProtection = loanPrincipal * (f.kreditProtection*1/100);
            var bungaDibayar = (f.sukuBunga*1/100)*loanPrincipal*f.lamaPinjaman;
            var bungaAssDibayar = (f.asstTlo*1/100)*hargaOTR;
            var maxRevenue = loanPrincipal - pengurangTerakhir - carDepreciation;
            var totalMaxRevenue = maxRevenue + cashBack;
            var payPerMonth = (bungaDibayar + loanPrincipal) / (f.lamaPinjaman*12);
            var cashNCarry = totalMaxRevenue - admInsurance - bungaAssDibayar - provisi - payPerMonth - kreditProtection;
            $('#payPerMonth').val(payPerMonth);
            $('#cashNCarry').val(cashNCarry);

            var ext = {
                cashBack: cashBack.formatPrice(),
                carDepreciation: carDepreciation.formatPrice(),
                pengurangTerakhir: pengurangTerakhir.formatPrice(),
                provisi: provisi.formatPrice(),
                bungaDibayar: bungaDibayar.formatPrice(),
                bungaAssDibayar: bungaAssDibayar.formatPrice(),
                admInsurance : admInsurance.formatPrice(),  
                hargaOTR : hargaOTR.formatPrice(),
                maxRevenue : maxRevenue.formatPrice(),
                totalMaxRevenue : totalMaxRevenue.formatPrice(),
                loanPrincipal : loanPrincipal.formatPrice(),
                payPerMonth : payPerMonth.formatPrice(),
                cashNCarry : cashNCarry.formatPrice(),
                kreditProtection : kreditProtection.formatPrice()
            };
            return $.extend(f,ext);
        },
        render: function(page,cb){
            console.log('render quote view');
            var that = this;
            var id = this.options.id;
            var data = {};
            var template;

            if(page=='form') {
                template=formTemplate;
            }
            else
                template=listTemplate;

            if(id) {
                template=formEditTemplate;

                console.log('load '+id);
                var quote = new Model({id:id});
                quote.fetch({
                    success: function (quote) {
                        var data = quote.toJSON();
                        that.model.set (data);
                        var compiledTemplate = _.template( template, data );
                        that.$el.html( compiledTemplate );
                        that.quoteForm = that.$el.find('#quoteForm');
                        that.initializeForm();
                        var status = that.model.get('status');
                        if(status && status=='Kontrak') {
                            that.$el.find('.save').addClass('hide');
                        }
                        else        
                            that.$el.find('.saveKontrak').removeClass('hide');

                        if(cb) cb();
                    }
                });
            }
            else {
                console.log(this.model.toJSON());
                var compiledTemplate = _.template( template, this.model.toJSON() );
                // Append our compiled template to this Views "el"
                this.$el.html( compiledTemplate );

                this.quoteForm = this.$el.find('#quoteForm');
               // console.log(this.$el.find('#quoteForm'));
                that.initializeForm();

                if(cb) cb();
            }

            

          

    },
    initializeForm:function(){
        that=this;

        that.$el.find('#bungaPinjaman').select2({data:{results:[]}});

        that.$el.find('#bungaAsuransi').select2({data:{results:[]}});

        this.$el.find('#kodeCustomer').select2({  
            ajax: {
                url: "/api/customers/search",
                quietMillis: 100,
                data: function (term, page) { 
                    return {
                        q:term
                    }
                },
                results : function(data) {
                    return {
                        results : $.map(data, function(item) {
                            return item;
                        })
                    };
                }
            },

            formatSelection:function(object,container) {
                if(typeof object!=='undefined' && object.length!=0) {
                    if(object.imgProfile)
                    {
                        $('#thumbnail-profile').attr('src','/thumbnail/'+object.imgProfile);
                    }
                    else {
                        $('#thumbnail-profile').attr('src','/img/80x80.gif');
                    }
                    $('#quoteAlamat').html(_.template( $("#alamatSection").html(),object.alamat));
                    $('#quoteTelepon').html(_.template( $("#teleponSection").html(),object.telepon));
                    that.customer =  object;
                }
                return object.text;
            },
            initSelection: function(element, callback) {
                var obj = that.model.get('customer');
                callback(obj); 
            }
            });

          
          this.$el.find('#perusahaanKredit').select2({  
            ajax: {
                url: "/api/perusahaanKredit/search",
                quietMillis: 100,
                data: function (term, page) { 
                    return {
                        q:term
                    }
                },
                results : function(data) {
                    return {
                        results : $.map(data, function(item) {
                            return item;
                        })
                    };
                }
            },

            formatSelection:function(object,container) {
                //console.log(object.obj);
                if(typeof object!=='undefined' && Object.prototype.toString.call( object ) !== '[object Array]') {

                    that.sukuBunga = object.sukuBunga;
                    that.perusahaanKredit = object;

                    var lamaPinjaman =  that.$el.find('#lamaPinjaman');

                    that.$el.find('#lamaPinjaman').trigger('change');
                    
                    var format=function format(item) { return item.jenis; };

                    var tahunBuat = that.$el.find('#tahunBuat').val()*1;

                    var filterBungaPinjaman = $.grep(object.bungaPinjaman, function(v) {
                        if(!v.tahunMulai && v.tahunAkhir)
                        {
                            return (tahunBuat<=(v.tahunAkhir*1));
                        }
                        else if(v.tahunMulai && !v.tahunAkhir)
                        {
                            return (tahunBuat>=(v.tahunMulai*1));   
                        }
                        else if(v.tahunMulai && v.tahunAkhir) {
                            return (tahunBuat>=(v.tahunMulai*1) && tahunBuat<=(v.tahunAkhir*1));
                        }
                        
                        return true;
                    });

                    that.$el.find('#bungaPinjaman').select2({
                        data:{
                            results:filterBungaPinjaman,
                            text:'jenis',id:'_id'
                        },
                        formatResult:format,
                        formatSelection:function(object) {
                            if(lamaPinjaman.val()==1) {
                                that.$el.find('#sukuBunga').val(object.thn1);
                            }
                            else if(lamaPinjaman.val()==2) {
                                that.$el.find('#sukuBunga').val(object.thn2);
                            }
                            else if(lamaPinjaman.val()==3) {
                                that.$el.find('#sukuBunga').val(object.thn3);
                            }
                            else if(lamaPinjaman.val()==4) {
                                that.$el.find('#sukuBunga').val(object.thn4);
                            }
                            that.$el.find('#percentDP').val(object.dp);
                            return object.jenis;
                        }
                    });

                    that.$el.find('#bungaAsuransi').select2({
                        data:{
                            results:object.bungaAsuransi,
                            text:'jenis',id:'_id'
                        },
                        formatResult:format,
                        formatSelection:function(object) {
                            if(lamaPinjaman.val()==1) {
                                that.$el.find('#asstTlo').val(object.thn1);
                            }
                            else if(lamaPinjaman.val()==2) {
                                that.$el.find('#asstTlo').val(object.thn2);
                            }
                            else if(lamaPinjaman.val()==3) {
                                that.$el.find('#asstTlo').val(object.thn3);
                            }
                            else if(lamaPinjaman.val()==4) {
                                that.$el.find('#asstTlo').val(object.thn4);
                            }
                            return object.jenis;
                        }
                    });

                    if(object.kreditProtection) {
                        that.$el.find('#kreditProtectionSection').show();
                    }
                    else {
                        that.$el.find('#kreditProtection').val(0);
                        that.$el.find('#kreditProtectionSection').hide();
                    }
                    return object.text;
                }

                return object.text;
            },
            initSelection: function(element, callback) {
                var id=$(element).val();
                if (id!=="") {
                   var obj = that.model.get('perusahaanKredit');
                   callback(obj); 

                }
            }
            });
        this.quoteForm.stepy({
            nextLabel:      'Forward <i class="icon-chevron-right icon-white"></i>',
            backLabel:      '<i class="icon-chevron-left"></i> Backward',
            block       : true,
            errorImage  : true,
            titleClick  : true,
            validate    : true,
            finish : function(){

                var cashBack = $('#cashBackMask').inputmask('unmaskedvalue');
                var carDepreciation = $('#carDepreciationMask').inputmask('unmaskedvalue');
                var pengurangDP = $('#pengurangDPMask').inputmask('unmaskedvalue');
                var pengurangTerakhir = $('#pengurangTerakhirMask').inputmask('unmaskedvalue');
                var provisi = $('#provisiMask').inputmask('unmaskedvalue');
                var admInsurance = $('#admInsuranceMask').inputmask('unmaskedvalue');
                var hargaOTR = $('#hargaOTRMask').inputmask('unmaskedvalue');
                
                $('#cashBack').val(cashBack);
                $('#carDepreciation').val(carDepreciation);
                $('#pengurangTerakhir').val(pengurangTerakhir);

                $('#pengurangDP').val(pengurangDP);
                $('#provisi').val(provisi);
                $('#admInsurance').val(admInsurance);
                $('#hargaOTR').val(hargaOTR);
                return false;
            },
             next: function(index) {
                if(index==3) {
                  var formData = that.quoteForm.serializeObject();
                  formData = that.calculateQuote(formData);
                  var tglTambah = that.model.get('tglTambah');
                  if(!tglTambah)
                    tglTambah = new Date();

                  //console.log('perusahaan kredit %j',that.perusahaanKredit);
                  formData = $.extend(formData,{tglTambah:tglTambah,inisial:that.perusahaanKredit.inisial,customer:that.customer});
                
                  var quotePrint = _.template( print1SectionTemplate ,formData);
                  that.$el.find('#quotePrint').html(quotePrint);
                }
             }
        });

        this.$el.find('.stepy-titles').each(function(){
            $(this).children('li').each(function(index){
                var myIndex = index + 1
                $(this).append('<span class="stepNb">'+myIndex+'</span>');
            })
        });

        this.quoteForm.find('.numeric').inputmask("integer");

        this.quoteForm.find('.percentage').inputmask("decimal");
        this.quoteForm.find('.price').on('focus',function(e){

        });
        this.quoteForm.find('.price').inputmask('Rp. 999.999.999.999', { numericInput: true,
            onKeyUp:function(e) {

                /*var code = e.keyCode || e.which;
                if (code == '8') {
                    $(this).val('');
                    return false;
                }*/
            }
        });
    }
                      
    });
    // Our module now returns our view
    return  QuoteView;
});