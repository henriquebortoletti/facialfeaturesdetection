import cv2
print('before video read')
vidcap = cv2.VideoCapture('my_video.mp4')
print('after video read')
success,image = vidcap.read()
count = 0
while success:
  cv2.imwrite("frames/frame%d.jpg" % count, image)     # save frame as JPEG file
  success,image = vidcap.read()
  print('Read a new frame: ', success)
  count += 1
