import random

if __name__ == '__main__':
    f = open("matrices", "w")

    for i in range(1800):
        for j in range(3):
            numbers = []
            for k in range(3):
                numbers.append(int(random.random() * 10))

            f.write('%d %d %d\n' % (numbers[0], numbers[1], numbers[2]))

        f.write('\n')

    f.close();
