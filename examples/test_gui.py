import PySimpleGUI as sg
import PyQt5

def main():
    # 定义布局
    layout = [
        [sg.Text('图层名称:'), sg.InputText(key='-INPUT-'), sg.Button('添加', key='-ADD-')],
        [sg.Listbox(values=[], size=(30, 10), key='-LAYER-LIST-', enable_events=True)],
        [sg.Button('上移', key='-UP-'), sg.Button('下移', key='-DOWN-'), sg.Button('退出')]
    ]

    # 创建窗口
    window = sg.Window('图层管理器', layout)

    # 初始化图层列表
    layer_list = []
    last_selected_index = None  # 记录最后一次选择的索引

    while True:
        event, values = window.read()
        if event == sg.WINDOW_CLOSED or event == '退出':
            break
        elif event == '-ADD-':
            new_layer = values['-INPUT-'].strip()
            if new_layer and new_layer not in layer_list:
                layer_list.append(new_layer)
                window['-LAYER-LIST-'].update(values=layer_list)
                window['-INPUT-'].update('')
        elif event == '-LAYER-LIST-':  # 当选择列表项时
            selected_indices = window['-LAYER-LIST-'].get_indexes()
            if selected_indices:
                last_selected_index = selected_indices[0]
        elif event == '-UP-':
            if last_selected_index is not None and last_selected_index > 0:
                layer_list[last_selected_index], layer_list[last_selected_index - 1] = layer_list[last_selected_index - 1], layer_list[last_selected_index]
                window['-LAYER-LIST-'].update(values=layer_list)
                # 更新 last_selected_index 以反映新的位置
                last_selected_index -= 1
        elif event == '-DOWN-':
            if last_selected_index is not None and last_selected_index < len(layer_list) - 1:
                layer_list[last_selected_index], layer_list[last_selected_index + 1] = layer_list[last_selected_index + 1], layer_list[last_selected_index]
                window['-LAYER-LIST-'].update(values=layer_list)
                # 更新 last_selected_index 以反映新的位置
                last_selected_index += 1

    window.close()

if __name__ == '__main__':
    main()