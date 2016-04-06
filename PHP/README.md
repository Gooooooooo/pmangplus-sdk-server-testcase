# P+ interface sample code for PHP

* modify identifier PmangPlusTest.php
* using PHPUnit
  * https://phpunit.de/getting-started.html
* phpunit install
> wget https://phar.phpunit.de/phpunit.phar
> chmod +x phpunit.phar
> sudo mv phpunit.phar /usr/local/bin/phpunit
> phpunit --version

* install composer
  * https://www.digitalocean.com/community/tutorials/how-to-install-and-use-composer-on-ubuntu-14-04
> sudo apt-get install curl php5-cli git
> curl -sS https://getcomposer.org/installer | sudo php -- --install-dir=/usr/local/bin --filename=composer

* composer install
> composer install

* testing
> phpunit UnitTest PmangPlusTest.php

