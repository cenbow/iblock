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
                .state('app',
                {
                	abstract:true,
                    views:
                    {
                        'header':
                        {
                            templateUrl: '/app/templates/header.html',
                            controller: 'HeaderController'
                        }
                    }
                }).state('app.home',
                {
                    url: '/',
                    views:
                    {
                        'main@':
                        {
                            templateUrl: '/app/templates/home.html'
                        }
                    }
                }).state('app.projects',
                {
                    url: '/projects?keyword&industry&city&minPay&maxPay',
                    views:
                    {
                        'main@':
                        {
                            templateUrl: '/app/templates/projects.html',
                            controller: 'ProjectListController'
                        },
                        'header@':
                        {
                            templateUrl: '/app/templates/header.html',
                            controller: 'HeaderController'
                        }
                    }
                    
                }).state('app.projectnew',
                {
                    url: '/newproject',
                    views:
                    {
                        'main@':
                        {
                            templateUrl:'/app/templates/newproject.html',
                            controller:'NewProjectController'
                        }
                    }
                }).state('app.designer',
                {
                    url: '/designer?keyword&industry&city&minPay&maxPay&skill',
                    views:
                    {
                        'main@':
                        {
                            templateUrl: '/app/templates/designer.html',
                            controller:'DesignerListController'
                        },
                        'header@':
                        {
                            templateUrl: '/app/templates/header.html',
                            controller: 'HeaderController'
                        }
                    }
                    
                }).state('app.myinfo',
                {
                    url:'/myinfo',
                    views:
                    {
                        'main@':
                        {
                            templateUrl:'/app/templates/myinfo.html',
                            controller: 'UserInfoController'
                        }
                    }
                }).state('app.msg',
                {
                    url:'/message',
                    views:
                    {
                        'main@':
                        {
                            templateUrl:'/app/templates/message.html',
                            controller: 'MsgController'
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
app.directive('ngEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function(event) {
                if(event.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.ngEnter, {'event': event});
                    });

                    event.preventDefault();
                }
            });
        };
    });
