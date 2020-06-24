/* globals Chart:false, feather:false */

(function () {
  'use strict'

  feather.replace()

  // Graphs
  var ctx = document.getElementById('myChart')
  // eslint-disable-next-line no-unused-vars
  var myChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: [
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
		'8',
		'9',
		'10',
		'11',
		'12',
		'13',
		'14',
		'15',
		'16',
		'17',
		'18',
		'19',
		'20',
		'21',
		'22',
		'23',
		'24',
		'25',
		'26',
		'27',
		'28',
		'29',
		'30'
      ],
      datasets: [{
        data: [
          1.5,
          1.6,
          1.7,
          1.8,
          1.9,
          1.10,
          1.11,
          1.12,
          1.13,
          1.14,
          2.68,
          2.69,
          2.70,
          2.71,
          2.72,
          2.73,
          2.74,
          2.75,
          2.76,
          2.77,
          5.8,
          5.9,
          5.10,
          5.11,
          5.12,
          5.13,
          5.14,
          5.15,
		  2.76,
		  2.77
        ],
        lineTension: 0,
        backgroundColor: 'transparent',
        borderColor: '#007bff',
        borderWidth: 4,
        pointBackgroundColor: '#007bff'
      }]
    },
    options: {
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: false
          }
        }]
      },
      legend: {
        display: false
      }
    }
  })
}())
