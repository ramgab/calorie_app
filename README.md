# Мобильное приложение для отслеживания питания и подсчета калорий
## Экран "О себе" и редактирование пользовательских данных
![mygif](https://github.com/ramgab/gif_for_app/blob/main/o_sebe.gif)


## Экран "Дневник"
![mygif](https://github.com/ramgab/gif_for_app/blob/main/dnevnik.gif)

## Взаимодействие с продуктами
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/gif_product.gif)

## Создание рецепта
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/gif_recipe.gif)


## Диаграмма классов
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/class_diagram.png)

## Диаграмма прецедентов
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/use_case_diagram_1.png)

## Диаграмма деятельности
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/activity_diagram.png)

## Диаграмма последовательности
![mygif](https://github.com/ramgab/calorie_app/blob/master/app/src/main/res/drawable/sequence_diagram.png)

## Парсер для сбора данных о продуктах
```
import requests
from bs4 import BeautifulSoup
import pandas as pd
import time  # Импорт модуля для работы с временем

# Функция для извлечения данных о продукте с одной страницы продукта
def extract_product_data(url):
    response = requests.get(url, proxies=proxies)  # Использование прокси-сервера для запроса
    soup = BeautifulSoup(response.content, 'html.parser')

    title_element = soup.find('h1', class_='product__title')
    title = title_element.text.strip() if title_element else "-"

    composition_element = soup.find('p', class_='kfBlRy')
    composition = composition_element.text.strip() if composition_element else "-"

    calories_element = soup.find('div', class_='product-calories-item__value')
    calories = calories_element.text.strip() if calories_element else "-"

    protein_elements = soup.find_all('div', class_='product-calories-item__value')
    protein = protein_elements[1].text.strip() if len(protein_elements) > 1 else "-"

    fat = protein_elements[2].text.strip() if len(protein_elements) > 2 else "-"
    carbohydrate = protein_elements[3].text.strip() if len(protein_elements) > 3 else "-"

    return {
        'Название продукта': title,
        'Состав': composition,
        'Калории': calories,
        'Белки': protein,
        'Жиры': fat,
        'Углеводы': carbohydrate
    }

# Извлечение ссылок на карточки продуктов
def extract_product_links():
    r = requests.get('https://www.perekrestok.ru/cat/c/16/likery')
    html = BeautifulSoup(r.content, 'html.parser')

    # Список для хранения ссылок
    product_links = []

    # Итерируемся по каждому элементу с классом 'product-card-wrapper'
    for el in html.select(".product-card-wrapper"):
        # Находим ссылку внутри элемента с классом 'product-card__link'
        link = 'https://www.perekrestok.ru' + el.select_one('.product-card__link')['href']
        # Добавляем ссылку в список
        product_links.append(link)
        #time.sleep(3)  # Задержка в 3 секунды между запросами
    return product_links

# Прокси-сервер

proxies = {
    'http': 'http://188.74.210.21:6100',
    'username': 'oijqplhm',
    'password': '3t635wlhf5gf'
}

# Получаем ссылки на карточки продуктов
product_links = extract_product_links()
print(product_links)

# Список для хранения данных о продуктах
products_data = []

# Извлечение данных о каждом продукте
for link in product_links:
    product_data = extract_product_data(link)
    products_data.append(product_data)

# Создание DataFrame с данными о продуктах
df = pd.DataFrame(products_data)

# Удаление дубликатов по столбцу 'Название продукта'
df = df.drop_duplicates(subset='Название продукта')

# Сохранение данных в файл Excel
df.to_excel('likery.xlsx', index=False)

```
