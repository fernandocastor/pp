# -*- coding: utf-8 -*-
import codecs
import re
import subprocess
import time

def getLockType(lockType):
    if lockType == 'r':
        return 'ReentrantLock'
    if lockType == 'a':
        return 'Additive backoff Lock'
    if lockType == 'e':
        return 'Exponential backoff Lock'
    if lockType == 'q':
        return 'Queue Lock'

    return 'No backoff Lock'


def measure(threads, counters, duration, limit, lockType, repetitions):
    elapsedTime = 0
    counts = {}
    for i in range(repetitions):
        start = time.time()
        output = subprocess.check_output(['java', '-jar', 'TAS.jar', str(threads), str(counters), str(duration),
                str(limit), lockType])
        elapsedTime += (time.time() - start)

        groups = re.findall('CounterThread Thread\[Thread-(\d+),.+\], counted: (\d+)', output)
        for count in groups:
            previous = counts[count[0]] if counts.has_key(count[0]) else 0
            counts[count[0]] = previous + int(count[1])

    f = codecs.open('results.txt', 'a', encoding='utf-8')
    f.write(u"Threads: %d, Contadores: %d, duração de cada experimento: %dms, Limite de contagem: %d (0 significa sem limite), Lock: %s\n" % (threads, counters, duration, limit, getLockType(lockType)))
    f.write(u"Repetições: %d\n" % repetitions)
    f.write(u"Tempo médio: %f segundos\n" % (elapsedTime / repetitions))
    meanCounts = {x: counts[x] / repetitions for x in counts.keys()}
    totalSum = sum(meanCounts.values())
    meanSum = totalSum / threads

    f.write("Total sum: %d\n" % totalSum)
    f.write("Threads mean count: %d\n" % meanSum)

    f.write('\n')
    f.close()


if __name__ == '__main__':
    measure(10, 10, 120000, 0, 'e', 30)
    measure(10, 10, 120000, 0, 'a', 30)
    measure(10, 10, 120000, 0, '', 30)
    measure(10, 10, 120000, 0, 'r', 30)
    measure(10, 10, 120000, 0, 'q', 30)

    measure(50, 10, 120000, 0, 'e', 30)
    measure(50, 10, 120000, 0, 'a', 30)
    measure(50, 10, 120000, 0, '', 30)
    measure(50, 10, 120000, 0, 'r', 30)
    measure(50, 10, 120000, 0, 'q', 30)

    measure(100, 10, 120000, 0, 'e', 30)
    measure(100, 10, 120000, 0, 'a', 30)
    measure(100, 10, 120000, 0, '', 30)
    measure(100, 10, 120000, 0, 'r', 30)
    measure(100, 10, 120000, 0, 'q', 30)

    measure(200, 10, 120000, 0, 'e', 30)
    measure(200, 10, 120000, 0, 'a', 30)
    measure(200, 10, 120000, 0, '', 30)
    measure(200, 10, 120000, 0, 'r', 30)
    measure(200, 10, 120000, 0, 'q', 30)

    measure(10, 10, 0, 1000, 'e', 30)
    measure(10, 10, 0, 1000, 'a', 30)
    measure(10, 10, 0, 1000, '', 30)
    measure(10, 10, 0, 1000, 'r', 30)
    measure(10, 10, 0, 1000, 'q', 30)

    measure(50, 10, 0, 1000, 'e', 30)
    measure(50, 10, 0, 1000, 'a', 30)
    measure(50, 10, 0, 1000, '', 30)
    measure(50, 10, 0, 1000, 'r', 30)
    measure(50, 10, 0, 1000, 'q', 30)

    measure(100, 10, 0, 1000, 'e', 30)
    measure(100, 10, 0, 1000, 'a', 30)
    measure(100, 10, 0, 1000, '', 30)
    measure(100, 10, 0, 1000, 'r', 30)
    measure(100, 10, 0, 1000, 'q', 30)

    measure(200, 10, 0, 1000, 'e', 30)
    measure(200, 10, 0, 1000, 'a', 30)
    measure(200, 10, 0, 1000, '', 30)
    measure(200, 10, 0, 1000, 'r', 30)
    measure(200, 10, 0, 1000, 'q', 30)
