let ip = '192.168.1.102';
function request(type){
    if(type == 1){
       
        $.get("http://"+ip+":8083/api/mqttmanualabre", function(data, status){
            $("#alerta").html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> Ventana abierta. </div>'); 
        });    
    }else if(type == 2){
        $.get("http://"+ip+":8083/api/mqttmanualcierra", function(data, status){
            $("#alerta").html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> Ventana cerrada. </div>');
        }); 
    }else if(type == 3){
        $.get("http://"+ip+":8083/api/mqttautomatico", function(data, status){
            $("#alerta").html('<div class="alert alert-info alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> Ventana en automatico. </div>');
        }); 
    }
}
$(function(){
  
   $(document).ready(function() {
       
        function getRandomArbitrary(min, max) {
            return Math.round(Math.random() * (max - min) + min);
          }
        let luz = [];
        let lluvia = [];
        var chart = new CanvasJS.Chart("chartContainer", {
          animationEnabled: true,
          theme: "light2",
          title:{
              text: "Grafica Sensores"
          },
          axisX:{
              valueFormatString: "H:m:s",
              crosshair: {
                  enabled: true,
                  snapToDataPoint: false
              }
          },
          axisY: {
              title: "Valor sensor",
              crosshair: {
                  enabled: true
              }
          },
          toolTip:{
              shared:true
          },  
          legend:{
              cursor:"pointer",
              verticalAlign: "bottom",
              horizontalAlign: "left",
              dockInsidePlotArea: true,
              itemclick: toogleDataSeries
          },
          data: [{
              type: "line",
              showInLegend: true,
              name: "Luz",
              markerType: "square",
              xValueFormatString: "H:m:s",
              color: "#F08080",
              dataPoints: luz
          },
          {
              type: "line",
              showInLegend: true,
              name: "Lluvia",
              lineDashType: "dash",
              dataPoints: lluvia
          }]
      });
      chart.render();
      
      function toogleDataSeries(e){
          if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
              e.dataSeries.visible = false;
          } else{
              e.dataSeries.visible = true;
          }
          chart.render();
      }
      setInterval(function(){
        $.getJSON("http://"+ip+":8083/api/sensores/7", function(data, status){
            if(typeof data != undefined && data != ''){
                if(luz.length == 10){luz.splice(0,1);}
                if(luz.length == 0){
                    var x = 0;
                }else{
                    var x = luz[luz.length-1]['x'] +1;
                }
                luz.push({label:moment().format('H:m:s'),x:x,y:data.valor});
                chart.render();
            }
           
        });
       
    },5000);
    setInterval(function(){
        $.getJSON("http://"+ip+":8083/api/sensores/5", function(data, status){
            console.log(data)
            if(typeof data != undefined && data != ''){
                if(lluvia.length == 10){lluvia.shift()}
                if(lluvia.length == 0){
                    var x = 0;
                }else{
                    var x = lluvia[lluvia.length-1]['x'] +1;
                }
                
                lluvia.push({label:moment().format('H:m:s'),x:x,y:data.valor});
                chart.render();
            }
        });
    },5000);
      
      });
 

});