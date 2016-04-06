# P+ interface sample code for python

* create a virtual env
> virtualenv env

* active virtual env
> source env/bin/activate

* install requirements pacakge
> pip install -r requirements.txt

* configuration for p+
* 1. application identifier
* 2. user identifier
* 3. payment identifier
* 4. target url(QA/TQ/LIVE)
> vi test_pmangplus.py

* running sample
py.test -q test_pmangplus.py

* deactivate virtual env
> deactivate
