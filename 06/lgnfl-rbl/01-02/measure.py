#!/usr/bin/python2
# -*- coding: utf-8 -*-
import codecs
import re
import subprocess
import time

def lock_type(lockType):
    if lockType == 'r':
        return 'ReentrantLock'
    if lockType == 'a':
        return 'TASLock com additive backoff'
    if lockType == 'e':
        return 'TASLock com exponential backoff'

    return 'TASLock sem backoff'

def measure_execution_test(threads, lock, nexecutions, repetitions):
    elapsedTime = 0
    counts = {}
    cmd = ['java', 'SpinLocking', str(threads), lock, 'execution', str(nexecutions)]
    print cmd
    for i in range(repetitions):
        start = time.time()
        output = subprocess.check_output(cmd)
        elapsedTime += (time.time() - start)

    f = codecs.open('results.txt', 'a', encoding='utf-8')
    f.write(u'Threads: %d, Limite de contagem por thread: %d, Lock: %s\n' % (threads, nexecutions, lock_type(lock)))
    f.write(u'Repetições: %d\n' % repetitions)
    f.write(u'Tempo médio: %f s\n' % (elapsedTime / repetitions))

    f.write('\n')
    f.close()

def measure_time_test(threads, lock, duration, repetitions):
    print '[TIME TEST] threads: %d, lock: %c, duration: %d, repetitions: %d' % (threads, lock, duration, repetition)
    elapsedTime = 0
    counts = {}
    cmd = ['java', 'SpinLocking', str(threads), lock, 'time', str(duration)]

    for i in range(repetitions):
        output = subprocess.check_output(cmd)

        groups = re.findall('Thread (\d+): (\d+)', output)
        for count in groups:
            previous = counts[count[0]] if counts.has_key(count[0]) else 0
            counts[count[0]] = previous + int(count[1])

    f = codecs.open('results.txt', 'a', encoding='utf-8')
    f.write(u'Threads: %d, Duração da contagem: %d, Lock: %s\n' % (threads, duration, lock_type(lock)))
    f.write(u'Repetições: %d\n' % repetitions)

    mean_count_per_thread = {x: counts[x] / repetitions for x in counts.keys()}
    mean_count = sum(mean_count_per_thread.values()) / threads
    f.write(u'Média de contagem de cada thread: %d\n' % mean_count)

    f.write('\n')
    f.close()


if __name__ == '__main__':
    measure_time_test(10, 'e', 2, 10)
    measure_time_test(10, 'a', 2, 10)
    measure_time_test(10, '', 2, 10)
    measure_time_test(10, 'r', 2, 10)

    measure_time_test(50, 'e', 2, 10)
    measure_time_test(50, 'a', 2, 10)
    measure_time_test(50, '', 2, 10)
    measure_time_test(50, 'r', 2, 10)

    measure_time_test(100, 'e', 2, 10)
    measure_time_test(100, 'a', 2, 10)
    measure_time_test(100, '', 2, 10)
    measure_time_test(100, 'r', 2, 10)

    measure_time_test(200, 'e', 2, 10)
    measure_time_test(200, 'a', 2, 10)
    measure_time_test(200, '', 2, 10)
    measure_time_test(200, 'r', 2, 10)

    measure_execution_test(10, 'e', 1000, 10)
    measure_execution_test(10, 'a', 1000, 10)
    measure_execution_test(10, '', 1000, 10)
    measure_execution_test(10, 'r', 1000, 10)

    measure_execution_test(50, 'e', 1000, 10)
    measure_execution_test(50, 'a', 1000, 10)
    measure_execution_test(50, '', 1000, 10)
    measure_execution_test(50, 'r', 1000, 10)

    measure_execution_test(100, 'e', 1000, 10)
    measure_execution_test(100, 'a', 1000, 10)
    measure_execution_test(100, '', 1000, 10)
    measure_execution_test(100, 'r', 1000, 10)

    measure_execution_test(200, 'e', 1000, 10)
    measure_execution_test(200, 'a', 1000, 10)
    measure_execution_test(200, '', 1000, 10)
    measure_execution_test(200, 'r', 1000, 10)
