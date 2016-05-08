'use strict';

/* App Module */
var app = angular.module('app', 
    //Modules
    [
        'lumx',
        'ui.router',
        'commonFilters',
        'iblockFilters',
        'rest',
        'DefinedServices',
        'ngCookies'

     ]);

app.config(function($locationProvider, $stateProvider, $urlRouterProvider){
    //$urlRouterProvider.otherwise('/');
      
	$locationProvider.html5Mode(
            {
                enabled: true,
                requireBase: false
            });

            $stateProvider
                .state('signup',
                {
                    url: '/signup/:role',
                    views:
                    {
                        'main@':
                        {
                            templateUrl: '/app/templates/signup.html',
                            controller:'SignupFormControll'
                        }
                    }
                })
                
}).run(['$rootScope', '$state', function($rootScope, $state)
        {
            $rootScope.$state = $state;
            //$rootScope.LayoutService = LayoutService;

            $rootScope.$on('$stateChangeStart', function(event, toState, toParams)
            {
                if (toState.redirectTo)
                {
                    event.preventDefault();
                    $state.go(toState.redirectTo, toParams)
                }
            });
        }]);;

app.directive( 'myHref', function ( $location ) {
  return function ( scope, element, attrs ) {
    var path;

    attrs.$observe( 'myHref', function (val) {
      path = val;
    });

    element.bind( 'click', function () {
      scope.$apply( function () {
        $location.path( path );
        window.location.href = path;
      });
    });
  };
});

