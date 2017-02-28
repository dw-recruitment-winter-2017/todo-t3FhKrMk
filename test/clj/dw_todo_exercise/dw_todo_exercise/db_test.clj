(ns dw-todo-exercise.dw-todo-exercise.db-test
  (:require [clojure.test :refer :all]
            [dw-todo-exercise.db :refer :all]
            [dw-todo-exercise.util :as util])
  (:refer-clojure :exclude [get]))

(deftest get-test
  (testing "returns nil when no todo exists for given id"
    (let [db (atom {})]
      (is (nil? (get @db :foo)))))
  (testing "returns the todo when found"
    (let [todo {:title "Foo", :complete false}
          db (atom {})
          created (create! db todo)
          id (:id created)]
      (is (= created (get @db id))))))

(deftest get-all-test
  (testing "returns empty seq with empty db"
    (let [db (atom {})]
      (is (empty? (get-all @db)))))
  (testing "returns all todos in db"
    (let [db (atom {})
          todo1 (create! db {:title "Number one", :complete false})
          todo2 (create! db {:title "Number two", :complete false})]
      (is (= #{todo1 todo2}
             (set (get-all @db)))))))

(deftest update!-test
  (testing "inserts new todo when none exists for the id"
    (let [todo {:title "New TODO", :complete false}
          id (util/title->id (:title todo))
          db (atom {})]
      (update! db id todo)
      (is (= (assoc todo :id id)
             (get @db id)))))
  (testing "updates existing todo when one exists for the id"
    (let [todo {:title "Existing TODO", :complete false}
          db (atom {})
          existing (create! db todo)
          id (:id existing)]
      (update! db id {:complete true})
      (is (= (assoc existing :complete true)
             (get @db id))))))

(deftest create!-test
  (testing "inserts new todo into db"
    (let [todo {:title "New TODO", :complete false}
          db (atom {})
          created (create! db todo)]
      (is (= todo
             (dissoc (get @db (:id created)) :id :created-at))))))

(deftest delete!-test
  (testing "deletes the todo when it exists"
    (let [db (atom {})
          todo (create! db {:title "Delete me" :complete true})]
      (delete! db (:id todo))
      (is (empty? (get-all @db))))))


