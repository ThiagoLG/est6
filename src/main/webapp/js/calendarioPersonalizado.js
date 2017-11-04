$(document).ready(function(){
	$("#calendar").datepicker({
		dateFormat: "dd-mm-yy",
		minDate: new Date(),
		maxDate: +365,
		dateFormat: "dd-mm-yy",
		monthNames: [ "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" ],
		dayNames: ["Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"],
		dayNamesShort: ["D","S","T","Q","Q","S","S"],
		dayNamesMin: ["D","S","T","Q","Q","S","S"],
		beforeShowDay: function(date){
			var day = date.getDay();
			if(day==0 || day==6){
				return [false];
			}
			else{
				return [true];
			}
		}
	});
});