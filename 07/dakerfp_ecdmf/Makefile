#
# Makefile
# Emiliano Firmino, 2015-04-25 11:20
#

all:
	@mkdir -p l i d f lv iv fv dv
	@cpp -DTYPE=long   -DVOLATILE="" StatCounter.src | sed '/^#/ d' > l/StatCounter.java
	@cpp -DTYPE=int    -DVOLATILE="" StatCounter.src | sed '/^#/ d' > i/StatCounter.java
	@cpp -DTYPE=double -DVOLATILE="" StatCounter.src | sed '/^#/ d' > d/StatCounter.java
	@cpp -DTYPE=float  -DVOLATILE="" StatCounter.src | sed '/^#/ d' > f/StatCounter.java
	@cpp -DTYPE=long   -DVOLATILE="volatile" StatCounter.src | sed '/^#/ d' > lv/StatCounter.java
	@cpp -DTYPE=int    -DVOLATILE="volatile" StatCounter.src | sed '/^#/ d' > iv/StatCounter.java
	@cpp -DTYPE=double -DVOLATILE="volatile" StatCounter.src | sed '/^#/ d' > dv/StatCounter.java
	@cpp -DTYPE=float  -DVOLATILE="volatile" StatCounter.src | sed '/^#/ d' > fv/StatCounter.java
	@cd l; javac *.java; cd ..
	@cd i; javac *.java; cd ..
	@cd d; javac *.java; cd ..
	@cd f; javac *.java; cd ..
	@cd lv; javac *.java; cd ..
	@cd iv; javac *.java; cd ..
	@cd dv; javac *.java; cd ..
	@cd fv; javac *.java; cd ..

clean:
	@rm -r l i d f lv iv fv dv

