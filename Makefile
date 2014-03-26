LIBS = $(shell find lib -name "*.jar" -exec printf :{} ';')
SOURCES = $(shell find src -name "*.java" -exec printf ' '{} ';')

all: compile run

.PHONY: compile run

compile:
	javac $(SOURCES) -d classes -cp $(LIBS)
	
run:
	java -cp $(LIBS):classes webservicesapi.Main

