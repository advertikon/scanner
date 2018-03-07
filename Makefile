all: c_scanner, c_stat

c_stat:
		javac -d bin com/ua/advertikon/scanner/stat.java

c_scanner:
		javac -d bin com/ua/advertikon/scanner/scanner.java

stat:
		java -cp bin:. com.ua.advertikon.scanner.stat > /dev/null

d_stat:
		java -cp bin:. com.ua.advertikon.scanner.stat

scanner:
		java -cp bin:. com.ua.advertikon.scanner.scanner > /dev/null

d_scanner:
		java -cp bin:. com.ua.advertikon.scanner.scanner