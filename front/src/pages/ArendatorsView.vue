<template>
  <div class="container">
    <div class="sidebar"></div>
    <div class="right">
      <div class="topnav">
        <div class="topnav-title">
          <h2>Арендаторы</h2>
          <div class="count indi">100</div>
        </div>
        <div class="topnav-right">
          <img
            src="./icons/notify_icon.png"
            alt="notify_icon"
            width="24px"
            height="24px"
          />
          <img
            src="./icons/help_icon.png"
            alt="help_icon"
            width="24px"
            height="24px"
          />
          <img
            src="./icons/user_icon.png"
            alt="user_icon"
            width="32px"
            height="32px"
          />
        </div>
      </div>
      <div class="infobar">
        <div class="info-search">
          <div class="search-part">
            <button class="btn-tbl ff-500-14">
              <img src="./icons/data-table_icon.png" alt="data-table_icon" />
            </button>
            <div class="search">
              <div class="filter">
                <img
                  src="./icons/filter_icon.png"
                  alt="filter_icon"
                  width="16px"
                  height="16px"
                />
                <select name="select" id="">
                  <option value="all">All</option>
                </select>
              </div>
              <div class="search-input">
                <img
                  src="./icons/search_icon.png"
                  alt="search_icon"
                  width="16px"
                  height="16px"
                />
                <input type="text" name="search" id="" placeholder="Search" />
                <img
                  src="./icons/shortcut_icon.png"
                  alt="shortcut_icon"
                  width="16px"
                  height="16px"
                />
              </div>
            </div>
          </div>
          <button class="btn-add ff-500-14">
            <a href="add_arendator.html" class="white">+ Добавить арендатора</a>
          </button>
        </div>
        <div class="info-stat">
          <div class="info-item">
            <div class="info-item-top">
              <div class="info-text"><a href="">Все</a></div>
              <div class="count indi">27</div>
            </div>
            <div class="info-select"><hr /></div>
          </div>
          <div class="info-item">
            <div class="info-item-top">
              <div class="info-text"><a href="">Общие</a></div>
              <div class="count indi">5</div>
            </div>
            <!-- <div class="info-select"><hr></div> -->
          </div>
          <div class="info-item">
            <div class="info-item-top">
              <div class="info-text"><a href="">Неактивные</a></div>
              <div class="count indi">3</div>
            </div>
            <!-- <div class="info-select"><hr></div> -->
          </div>
        </div>
      </div>

      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>ФАМИЛИЯ</th>
              <th>ИМЯ</th>
              <th>ТЕЛЕФОН</th>
              <th>ПРИМЕЧАНИЯ</th>
              <th>ДЕЙСТВИЕ</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>-></td>
              <td><a href="#">Литвиненко</a></td>
              <td><a href="#">И.А.</a></td>
              <td><a href="#">1111111111</a></td>
              <td></td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Зиньковский</a></td>
              <td><a href="#">Д.В.</a></td>
              <td><a href="#">068-7016066</a></td>
              <td>посмотреть, как сохраняется длинный текст в журнал</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Храмешкина</a></td>
              <td><a href="#">М.А.</a></td>
              <td><a href="#">0503578038</a></td>
              <td>Канцтовары</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Петренко</a></td>
              <td><a href="#">Т.В.</a></td>
              <td><a href="#">0636548031</a></td>
              <td>Дитячий одяг</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Червяковський</a></td>
              <td><a href="#">В.П.</a></td>
              <td><a href="#">0679650645</a></td>
              <td>Зачинено</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Рыбалко</a></td>
              <td><a href="#">Т.А.</a></td>
              <td><a href="#">1111111111111</a></td>
              <td>Стоматологія</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Гаппаров</a></td>
              <td><a href="#">А.Г.</a></td>
              <td><a href="#">1111111111111</a></td>
              <td>Сухофрукт</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Марина</a></td>
              <td><a href="#">Ю.В.</a></td>
              <td><a href="#">0679532836</a></td>
              <td>Чай, кава</td>
              <td><a href="#">---</a></td>
            </tr>
            <tr>
              <td>-></td>
              <td><a href="#">Хамроев</a></td>
              <td><a href="#">Жасур</a></td>
              <td><a href="#">0985966608</a></td>
              <td>Сухофрукт</td>
              <td><a href="#">---</a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>