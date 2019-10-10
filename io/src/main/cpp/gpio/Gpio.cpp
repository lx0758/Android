#include "Gpio.h"

void *runPoll(void *data);

Gpio::Gpio(int number) {
    this->number = number;
    this->info = NULL;
}

int Gpio::gpio_export() {
    char buffer[64];
    int len = snprintf(buffer, sizeof(buffer), "%d", number);
    int result = write_file("/sys/class/gpio/export", buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_unexport() {
    char buffer[64];
    int len = snprintf(buffer, sizeof(buffer), "%d", number);
    int result = write_file("/sys/class/gpio/unexport", buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_direction(char direction) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/direction", number);
    int result = write_file(path, &direction, sizeof(direction));
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_edge(char edge) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/edge", number);
    int result = write_file(path, &edge, sizeof(edge));
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_write(int value) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", number);
    char buffer[1];
    int len = snprintf(buffer, sizeof(buffer), "%d", value);
    int result = write_file(path, buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_read() {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", number);
    char buffer[64];
    int result = read_file(path, buffer, 64);
    if (result != 0) return -1;
    return atoi(buffer);
}

int Gpio::gpio_start_poll(void (*pFunction)(int, int)) {
    gpio_stop_poll();
    Info *newInfo = new Info();
    newInfo->runing = true;
    newInfo->number = number;
    newInfo->callback = pFunction;
    pthread_create(&newInfo->pthread, NULL, runPoll, newInfo);
    info = newInfo;
    return 0;
}

int Gpio::gpio_stop_poll() {
    if (info != NULL) {
        info->runing = false;
        info = NULL;
    }
    return 0;
}

void *runPoll(void *data) {
    Info *info = (Info *) data;

    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", info->number);
    int fd = open(path, O_RDONLY);
    if(fd <= 0) {
        info->callback(TYPE_ERROR, -1);
        close(fd);
        pthread_exit(&info->pthread);
    }

    int result = 0;
    bool first = true;
    struct pollfd fds[1];
    fds[0].fd = fd;
    fds[0].events = POLLPRI;
    while (info->runing) {
        result = poll(fds, 1, 1000);
        if (result < 0) {
            if (info->runing) info->callback(TYPE_ERROR, -1);
            break;
        }
        if (result == 0) continue;
        if(fds[0].revents & POLLPRI) {
            usleep(10 * 1000);
            char buffer[16];
            if (lseek(fd, 0, SEEK_SET) == -1 || read(fd, buffer, sizeof(buffer)) == -1) {
                if (info->runing) info->callback(TYPE_ERROR, -1);
                break;
            }
            if (first) {
                first = false;
                continue;
            }
            if (info->runing) info->callback(TYPE_EVENT, atoi(buffer));
        }
    }

    close(fd);
    pthread_exit(&info->pthread);
}

int Gpio::read_file(const char *path, char *buffer, int len) {
    int fd = open(path, O_RDONLY);
    if (fd < 0) return -1;
    if (read(fd, buffer, len) < 0) return -1;
    close(fd);
    return 0;
}

int Gpio::write_file(const char *path, char *buffer, int len) {
    int fd = open(path, O_WRONLY);
    if (fd < 0) return -1;
    if (write(fd, buffer, len) < 0) return -1;
    close(fd);
    return 0;
}
