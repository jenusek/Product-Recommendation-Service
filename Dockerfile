FROM mysql:5.7

COPY ./wait-for-mysql.sh /usr/local/bin/wait-for-mysql.sh
