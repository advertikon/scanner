

c_stat: clean
		javac -d ./bin com/ua/advertikon/scanner/stat.java

c_scanner: clean
		javac -d ./bin com/ua/advertikon/scanner/scanner.java

stat:
		java -cp bin:. com.ua.advertikon.scanner.stat > /dev/null

d_stat:
		java -cp bin:. com.ua.advertikon.scanner.stat

scanner:
		java -cp bin:. com.ua.advertikon.scanner.scanner > /dev/null

scanner_full:
		java -cp bin:. com.ua.advertikon.scanner.scanner full > /dev/null

scanner_popular:
		java -cp bin:. com.ua.advertikon.scanner.scanner popular > /dev/null

d_scanner:
		java -cp bin:. com.ua.advertikon.scanner.scanner


c_console: clean
		javac -d bin com/ua/advertikon/console/Console.java

d_console:
		java -cp bin:. com.ua.advertikon.console.Console

console:
		java -cp bin:. com.ua.advertikon.console.Console > /dev/null



clean:
		rm -fR bin/*